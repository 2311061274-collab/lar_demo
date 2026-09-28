import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SplitVetcenter {
    public static void main(String[] args) throws IOException {
        String inputPath = "vetcenter.html";
        List<String> lines = Files.readAllLines(Paths.get(inputPath), StandardCharsets.UTF_8);

        StringBuilder headerContent = new StringBuilder();
        StringBuilder mainContent = new StringBuilder();
        StringBuilder footerContent = new StringBuilder();

        int state = 0; // 0 = before header, 1 = header, 2 = main, 3 = footer

        for (String line : lines) {
            if (line.contains("<div data-elementor-type=\"header\"")) {
                state = 1;
            } else if (line.contains("<div id=\"main\" role=\"main\"")) {
                state = 2;
            } else if (line.contains("<footer id=\"main-footer\"")) {
                state = 3;
            }

            if (state == 1) {
                headerContent.append(line).append("\n");
            } else if (state == 2) {
                mainContent.append(line).append("\n");
            } else if (state == 3) {
                footerContent.append(line).append("\n");
            } else if (state == 0) {
                headerContent.append(line).append("\n");
            }
        }

        // Write header
        String headerHtml = "<!DOCTYPE html>\n<html lang=\"vi\" xmlns:th=\"http://www.thymeleaf.org\">\n<body>\n<div th:fragment=\"header\">\n" 
                            + headerContent.toString() 
                            + "\n</div>\n</body>\n</html>";
        Files.write(Paths.get("src/main/resources/templates/fragments/header.html"), headerHtml.getBytes(StandardCharsets.UTF_8));

        // Write footer
        String footerHtml = "<!DOCTYPE html>\n<html lang=\"vi\" xmlns:th=\"http://www.thymeleaf.org\">\n<body>\n<div th:fragment=\"footer\">\n" 
                            + footerContent.toString() 
                            + "\n</div>\n</body>\n</html>";
        Files.write(Paths.get("src/main/resources/templates/fragments/footer.html"), footerHtml.getBytes(StandardCharsets.UTF_8));

        // For home, we inject the fragments
        String homeHtml = "<!DOCTYPE html>\n" +
                "<html lang=\"vi\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>PetCare - Trang chủ</title>\n" +
                "</head>\n" +
                "<body class=\"home wp-singular page-template-default page page-id-988584 wp-custom-logo wp-embed-responsive wp-theme-vamtam-petmania wp-child-theme-petmania-child full header-layout-logo-menu has-page-header no-middle-header responsive-layout has-post-thumbnail vamtam-is-elementor elementor-active elementor-pro-active vamtam-wc-cart-empty wc-product-gallery-slider-active layout-full elementor-default elementor-kit-16 elementor-page elementor-page-988584 e--ua-blink e--ua-chrome e--ua-webkit\">\n" +
                "<div th:replace=\"~{fragments/header :: header}\"></div>\n" +
                mainContent.toString() +
                "<div th:replace=\"~{fragments/footer :: footer}\"></div>\n" +
                "</body>\n</html>";

        Files.write(Paths.get("src/main/resources/templates/home.html"), homeHtml.getBytes(StandardCharsets.UTF_8));
        System.out.println("Split complete!");
    }
}
