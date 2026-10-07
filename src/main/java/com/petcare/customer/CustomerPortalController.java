package com.petcare.customer;

import com.petcare.appointment.Appointment;
import com.petcare.appointment.AppointmentRepository;
import com.petcare.auth.AppUser;
import com.petcare.auth.AppUserRepository;
import com.petcare.followup.PetDailyFollowup;
import com.petcare.followup.PetDailyFollowupRepository;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerPortalController {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final PetDailyFollowupRepository petDailyFollowupRepository;

    private Customer resolveCurrentCustomer(Principal principal) {
        if (principal == null) return null;
        String identifier = principal.getName();
        AppUser user = appUserRepository.findByUsername(identifier)
                .or(() -> appUserRepository.findByPhone(identifier))
                .orElse(null);

        if (user == null) return null;

        if (user.getCustomer() != null) {
            return user.getCustomer();
        }

        // Try to link by phone or create new customer
        String phone = user.getPhone() != null ? user.getPhone() : user.getUsername();
        Customer customer = customerRepository.findFirstByPhone(phone).orElseGet(() -> {
            Customer c = new Customer();
            c.setFullName(user.getFullName() != null ? user.getFullName() : user.getUsername());
            c.setPhone(phone);
            return customerRepository.save(c);
        });

        user.setCustomer(customer);
        appUserRepository.save(user);
        return customer;
    }

    @GetMapping("/history")
    public String showAppointmentHistory(
            Principal principal,
            @RequestParam(value = "followupSuccess", required = false) Boolean followupSuccess,
            Model model
    ) {
        Customer customer = resolveCurrentCustomer(principal);
        if (customer == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentRepository
                .findByCustomer_IdOrderByAppointmentDateDescStartTimeDesc(customer.getId());

        List<Appointment> revisitAlerts = appointmentRepository
                .findByCustomer_IdAndRevisitDateIsNotNullOrderByRevisitDateAsc(customer.getId())
                .stream()
                .filter(a -> a.getRevisitDate() != null && !a.getRevisitDate().isBefore(LocalDate.now()))
                .toList();

        List<PetDailyFollowup> followups = petDailyFollowupRepository
                .findByCustomerIdOrderByCreatedAtDesc(customer.getId());

        List<Pet> pets = petRepository.findByCustomer_IdOrderByIdAsc(customer.getId());

        model.addAttribute("customer", customer);
        model.addAttribute("appointments", appointments);
        model.addAttribute("revisitAlerts", revisitAlerts);
        model.addAttribute("followups", followups);
        model.addAttribute("pets", pets);
        model.addAttribute("today", LocalDate.now());
        if (Boolean.TRUE.equals(followupSuccess)) {
            model.addAttribute("successMsg", "Đã cập nhật tình trạng sức khỏe thú cưng thành công! Bác sĩ PetCare sẽ theo dõi và hỗ trợ bạn.");
        }

        return "customer/history";
    }

    @PostMapping("/followup")
    public String submitDailyFollowup(
            @RequestParam("appointmentId") Long appointmentId,
            @RequestParam("petId") Long petId,
            @RequestParam("dayNumber") Integer dayNumber,
            @RequestParam(value = "eatingStatus", defaultValue = "Ăn tốt") String eatingStatus,
            @RequestParam(value = "temperatureStatus", defaultValue = "Bình thường") String temperatureStatus,
            @RequestParam(value = "energyStatus", defaultValue = "Hoạt bát") String energyStatus,
            @RequestParam("symptomsNotes") String symptomsNotes,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        Customer customer = resolveCurrentCustomer(principal);
        if (customer == null) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        Pet pet = petRepository.findById(petId).orElse(null);

        if (appointment == null || pet == null || !appointment.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy hồ sơ ca khám tương ứng!");
            return "redirect:/customer/history";
        }

        PetDailyFollowup followup = petDailyFollowupRepository
                .findByAppointmentIdAndDayNumber(appointmentId, dayNumber)
                .orElseGet(PetDailyFollowup::new);

        boolean isUpdate = followup.getId() != null;
        if (isUpdate && followup.isConfirmedByAdmin()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Tình trạng ngày " + dayNumber + " đã được Bác sĩ / Phòng khám xác nhận nên không thể chỉnh sửa nữa!");
            return "redirect:/customer/history#nhat-ky-hoi-phuc";
        }

        if (!isUpdate) {
            followup.setAppointment(appointment);
            followup.setPet(pet);
            followup.setCustomer(customer);
            followup.setDayNumber(dayNumber);
            followup.setLogDate(LocalDate.now());
        }
        followup.setEatingStatus(eatingStatus);
        followup.setTemperatureStatus(temperatureStatus);
        followup.setEnergyStatus(energyStatus);
        followup.setSymptomsNotes(symptomsNotes.trim());
        followup.setCreatedAt(LocalDateTime.now());

        petDailyFollowupRepository.save(followup);

        appointment.setLastFollowupAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        String msg = isUpdate
                ? "Đã sửa và cập nhật lại nội dung theo dõi ngày " + dayNumber + " cho bé " + pet.getName() + " thành công!"
                : "Đã lưu nhật ký theo dõi ngày " + dayNumber + " cho bé " + pet.getName() + " thành công!";
        redirectAttributes.addFlashAttribute("successMsg", msg);
        return "redirect:/customer/history#nhat-ky-hoi-phuc";
    }

    @GetMapping("/pets")
    public String listPets(
            Principal principal,
            @RequestParam(value = "saved", required = false) Boolean saved,
            Model model
    ) {
        Customer customer = resolveCurrentCustomer(principal);
        if (customer == null) {
            return "redirect:/login";
        }

        List<Pet> pets = petRepository.findByCustomer_IdOrderByIdAsc(customer.getId());
        model.addAttribute("customer", customer);
        model.addAttribute("pets", pets);
        if (Boolean.TRUE.equals(saved)) {
            model.addAttribute("successMsg", "Lưu thông tin thú cưng thành công!");
        }

        return "customer/pets";
    }

    @PostMapping("/pets/save")
    public String savePet(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam(value = "breed", required = false) String breed,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "birthDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        Customer customer = resolveCurrentCustomer(principal);
        if (customer == null) {
            return "redirect:/login";
        }

        Pet pet;
        if (id != null) {
            pet = petRepository.findById(id).orElse(new Pet());
            if (pet.getCustomer() != null && !pet.getCustomer().getId().equals(customer.getId())) {
                redirectAttributes.addFlashAttribute("errorMsg", "Bạn không có quyền chỉnh sửa thú cưng này.");
                return "redirect:/customer/pets";
            }
        } else {
            pet = new Pet();
        }

        pet.setCustomer(customer);
        pet.setName(name.trim());
        pet.setSpecies(species);
        pet.setBreed(breed != null ? breed.trim() : null);
        pet.setGender(gender);
        pet.setBirthDate(birthDate);

        petRepository.save(pet);
        redirectAttributes.addFlashAttribute("successMsg", "Đã lưu hồ sơ thú cưng thành công!");
        return "redirect:/customer/pets?saved=true";
    }

    @PostMapping("/pets/{id}/delete")
    public String deletePet(
            @PathVariable("id") Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        Customer customer = resolveCurrentCustomer(principal);
        if (customer == null) {
            return "redirect:/login";
        }

        Pet pet = petRepository.findById(id).orElse(null);
        if (pet != null && pet.getCustomer().getId().equals(customer.getId())) {
            boolean hasAppointments = appointmentRepository.existsByPet_Id(id);
            if (hasAppointments) {
                redirectAttributes.addFlashAttribute("errorMsg", "Không thể xóa thú cưng này vì bé đã có lịch sử khám chữa bệnh tại viện.");
            } else {
                petRepository.delete(pet);
                redirectAttributes.addFlashAttribute("successMsg", "Đã xóa hồ sơ bé thành công.");
            }
        }
        return "redirect:/customer/pets";
    }
}
