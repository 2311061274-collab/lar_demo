# Test syntax of diagrams
diag_overview = """flowchart LR
    Guest(("Khách vãng lai<br/>(Guest)"))
    Customer(("Khách hàng<br/>(Customer)"))
    Admin(("Quản trị viên / Bác sĩ<br/>(Admin)"))
    Gmail["<< External >><br/>Gmail SMTP"]
    AiSys["<< External >><br/>Gemini AI"]

    Guest <|-- Customer

    subgraph PetCare_System ["HỆ THỐNG QUẢN LÝ BỆNH VIỆN THÚ Y PETCARE"]
        subgraph Sub_Auth ["1. Xác thực & Tài khoản"]
            UC_Reg(["Đăng ký tài khoản"])
            UC_Verify(["Xác thực OTP / Kích hoạt Gmail"])
            UC_Login(["Đăng nhập"])
            UC_Forgot(["Quên mật khẩu"])
            UC_Reset(["Đặt lại mật khẩu mới"])
            UC_Logout(["Đăng xuất"])
        end

        subgraph Sub_Public ["2. Khám phá & Tương tác công cộng"]
            UC_ViewHome(["Xem trang chủ & Giới thiệu"])
            UC_ViewService(["Tra cứu dịch vụ & Bảng giá"])
            UC_ViewNews(["Đọc cẩm nang & Tin tức thú cưng"])
            UC_ViewBranch(["Tra cứu danh sách chi nhánh"])
            UC_SendContact(["Gửi tin nhắn liên hệ / phản ánh"])
            UC_Chat(["Chat tư vấn trực tuyến"])
        end

        subgraph Sub_Customer ["3. Nghiệp vụ Khách hàng (User)"]
            UC_BookApp(["Đặt lịch hẹn khám / chăm sóc"])
            UC_CancelApp(["Yêu cầu hủy lịch hẹn"])
            UC_ViewHistory(["Xem lịch sử khám & Đơn thuốc"])
            UC_ManagePets(["Quản lý hồ sơ thú cưng"])
            UC_UpdateFollowup(["Ghi nhật ký tình trạng sau khám"])
            UC_PostReview(["Gửi & Chỉnh sửa đánh giá dịch vụ"])
        end

        subgraph Sub_Admin ["4. Nghiệp vụ Quản trị viên (Admin)"]
            UC_Dash(["Xem Dashboard & Thống kê tổng quan"])
            UC_ManageApp(["Quản lý lịch hẹn & Tiếp nhận khám"])
            UC_ConfirmFollowup(["Xác nhận nhật ký sau khám & Hẹn tái khám"])
            UC_ManageCustomer(["Quản lý danh sách khách hàng"])
            UC_ManagePetAdmin(["Quản lý hồ sơ bệnh án thú cưng"])
            UC_ManageServices(["Quản lý dịch vụ & Bảng giá"])
            UC_ManageBranches(["Quản lý chi nhánh"])
            UC_ManageNews(["Quản lý bài viết & Cẩm nang"])
            UC_ManageContacts(["Quản lý thư liên hệ"])
            UC_AdminChat(["Phản hồi tư vấn khách hàng (Live Chat)"])
            UC_SiteSettings(["Cấu hình thông tin phòng khám & Hotline"])
        end
    end

    Guest --- UC_Reg
    Guest --- UC_Verify
    Guest --- UC_Login
    Guest --- UC_Forgot
    Guest --- UC_Reset
    Guest --- UC_ViewHome
    Guest --- UC_ViewService
    Guest --- UC_ViewNews
    Guest --- UC_ViewBranch
    Guest --- UC_SendContact
    Guest --- UC_BookApp
    Guest --- UC_Chat

    Customer --- UC_Logout
    Customer --- UC_CancelApp
    Customer --- UC_ViewHistory
    Customer --- UC_ManagePets
    Customer --- UC_UpdateFollowup
    Customer --- UC_PostReview

    Admin --- UC_Dash
    Admin --- UC_ManageApp
    Admin --- UC_ConfirmFollowup
    Admin --- UC_ManageCustomer
    Admin --- UC_ManagePetAdmin
    Admin --- UC_ManageServices
    Admin --- UC_ManageBranches
    Admin --- UC_ManageNews
    Admin --- UC_ManageContacts
    Admin --- UC_AdminChat
    Admin --- UC_SiteSettings
    Admin --- UC_Logout

    UC_Reg -.->|<< include >>| Gmail
    UC_Forgot -.->|<< include >>| Gmail
    UC_Chat -.->|<< include >>| AiSys
"""

diag_customer = """flowchart TD
    Customer(("Khách hàng<br/>(Customer)"))
    Gmail["<< External >><br/>Gmail Service"]
    Gemini["<< External >><br/>Gemini AI"]

    subgraph Boundary_Customer ["HỆ THỐNG PETCARE - PHÂN RÃ NGHIỆP VỤ KHÁCH HÀNG"]
        subgraph Group_Auth ["1. Phân rã Quản lý Tài khoản & Xác thực"]
            UC_Register(["Đăng ký tài khoản"])
            UC_Validate_Reg(["Kiểm tra định dạng & Cho phép đăng ký lại nếu chưa kích hoạt"])
            UC_Send_Verify_OTP(["Gửi email mã OTP kích hoạt 6 số"])
            UC_Verify_Code(["Xác thực OTP tại /verify-code"])
            UC_Verify_Link(["Kích hoạt qua liên kết URL"])
            
            UC_Login(["Đăng nhập hệ thống"])
            UC_Route_Dashboard(["Điều hướng về Dashboard Khách hàng"])
            UC_Logout(["Đăng xuất an toàn"])
            
            UC_Forgot_Pwd(["Yêu cầu quên mật khẩu"])
            UC_Send_Reset_OTP(["Gửi OTP đặt lại mật khẩu 15 phút"])
            UC_Reset_Pwd(["Thiết lập mật khẩu mới >= 8 ký tự, chữ + số"])
        end

        subgraph Group_Appointment ["2. Phân rã Đặt lịch & Lịch hẹn"]
            UC_Book_App(["Đặt lịch hẹn khám / dịch vụ"])
            UC_Select_Service(["Chọn dịch vụ & Bác sĩ / Chi nhánh"])
            UC_Input_PetInfo(["Nhập thông tin thú cưng & Triệu chứng"])
            UC_Auto_Link_Cust(["Tự động liên kết hồ sơ theo SĐT/Email"])
            
            UC_View_History(["Xem lịch sử cuộc hẹn cá nhân"])
            UC_Cancel_App(["Hủy lịch hẹn khi PENDING"])
            UC_View_Prescription(["Xem đơn thuốc & Lời dặn của bác sĩ"])
        end

        subgraph Group_Pet_Followup ["3. Phân rã Hồ sơ Thú cưng & Nhật ký Sau khám"]
            UC_View_Pets(["Xem danh sách thú cưng của tôi"])
            UC_View_Pet_Detail(["Xem hồ sơ bệnh án trọn đời"])
            
            UC_Daily_Followup(["Cập nhật nhật ký sức khỏe sau khám hằng ngày"])
            UC_Input_Health(["Ghi chú ăn uống, vận động, vết thương"])
            UC_Edit_Followup(["Chỉnh sửa nhật ký trong ngày"])
            UC_Lock_Followup(["Khóa chỉnh sửa khi Bác sĩ đã duyệt"])
            UC_View_ReExam(["Xem lịch hẹn tái khám"])
        end

        subgraph Group_Interaction ["4. Phân rã Tương tác & Tư vấn trực tuyến"]
            UC_Live_Chat(["Tư vấn trực tuyến Live Chat"])
            UC_Attach_Image(["Đính kèm hình ảnh thú cưng"])
            UC_Chat_AI(["Tự động tư vấn bằng Gemini AI"])
            
            UC_Post_Review(["Gửi đánh giá dịch vụ Sao + Nhận xét"])
            UC_Edit_Review(["Chỉnh sửa đánh giá của chính mình"])
            UC_Send_Contact(["Gửi thư liên hệ / phản ánh"])
        end
    end

    Customer --> UC_Register
    UC_Register ..->|<< include >>| UC_Validate_Reg
    UC_Register ..->|<< include >>| UC_Send_Verify_OTP
    UC_Send_Verify_OTP --> Gmail
    Customer --> UC_Verify_Code
    Customer --> UC_Verify_Link
    Customer --> UC_Login
    UC_Login ..->|<< include >>| UC_Route_Dashboard
    Customer --> UC_Logout
    Customer --> UC_Forgot_Pwd
    UC_Forgot_Pwd ..->|<< include >>| UC_Send_Reset_OTP
    UC_Send_Reset_OTP --> Gmail
    Customer --> UC_Reset_Pwd
    UC_Reset_Pwd ..->|<< include >>| UC_Login

    Customer --> UC_Book_App
    UC_Book_App ..->|<< include >>| UC_Select_Service
    UC_Book_App ..->|<< include >>| UC_Input_PetInfo
    UC_Book_App ..->|<< include >>| UC_Auto_Link_Cust
    Customer --> UC_View_History
    UC_View_History ..->|<< extend >>| UC_Cancel_App
    UC_View_History ..->|<< extend >>| UC_View_Prescription

    Customer --> UC_View_Pets
    UC_View_Pets ..->|<< include >>| UC_View_Pet_Detail
    Customer --> UC_Daily_Followup
    UC_Daily_Followup ..->|<< include >>| UC_Input_Health
    UC_Daily_Followup ..->|<< extend >>| UC_Edit_Followup
    UC_Edit_Followup ..->|<< extend >>| UC_Lock_Followup
    UC_Daily_Followup ..->|<< extend >>| UC_View_ReExam

    Customer --> UC_Live_Chat
    UC_Live_Chat ..->|<< extend >>| UC_Attach_Image
    UC_Live_Chat ..->|<< extend >>| UC_Chat_AI
    UC_Chat_AI --> Gemini
    Customer --> UC_Post_Review
    UC_Post_Review ..->|<< extend >>| UC_Edit_Review
    Customer --> UC_Send_Contact
"""

diag_admin = """flowchart TD
    Admin(("Quản trị viên / Bác sĩ<br/>(Admin)"))

    subgraph Boundary_Admin ["HỆ THỐNG PETCARE - PHÂN RÃ NGHIỆP VỤ ADMIN / BÁC SĨ"]
        subgraph Group_Admin_App ["1. Phân rã Quản lý Lịch hẹn & Khám bệnh"]
            UC_AD_App_List(["Xem danh sách lịch hẹn toàn viện"])
            UC_AD_App_Filter(["Lọc lịch hẹn theo ngày, trạng thái, chi nhánh"])
            UC_AD_App_Confirm(["Xác nhận tiếp nhận lịch hẹn CONFIRMED"])
            UC_AD_App_Cancel(["Từ chối / Hủy lịch hẹn CANCELLED"])
            UC_AD_App_Exam(["Tiến hành khám bệnh & Điều trị"])
            UC_AD_Diagnosis(["Ghi nhận kết quả chẩn đoán bệnh án"])
            UC_AD_Prescribe(["Kê đơn thuốc & Hướng dẫn liều dùng"])
            UC_AD_Complete(["Hoàn thành ca khám COMPLETED"])
        end

        subgraph Group_Admin_Followup ["2. Phân rã Giám sát Sau điều trị & Tái khám"]
            UC_AD_View_Followup(["Xem danh sách nhật ký sau khám của thú cưng"])
            UC_AD_Contact_Cust(["Xem thông tin khách hàng để gọi điện / liên hệ"])
            UC_AD_Confirm_Status(["Xác nhận tình trạng sức khỏe sau khám"])
            UC_AD_Lock_User(["Kích hoạt khóa quyền sửa nhật ký của user"])
            UC_AD_Doctor_Note(["Ghi chú chỉ định điều trị của bác sĩ"])
            UC_AD_Set_ReExam(["Chỉ định lịch hẹn tái khám cho thú cưng"])
        end

        subgraph Group_Admin_Entity ["3. Phân rã Quản lý Khách hàng & Thú cưng"]
            UC_AD_Cust_List(["Quản lý danh sách khách hàng"])
            UC_AD_Cust_Detail(["Xem lịch sử khám & thú cưng sở hữu của khách"])
            
            UC_AD_Pet_List(["Quản lý danh mục thú cưng toàn viện"])
            UC_AD_Pet_Add(["Thêm mới hồ sơ thú cưng"])
            UC_AD_Pet_Edit(["Cập nhật thông tin: cân nặng, tuổi, mã chip"])
            UC_AD_Pet_History(["Xem chi tiết hồ sơ bệnh án trọn đời"])
        end

        subgraph Group_Admin_Chat ["4. Phân rã Trực Live Chat Bác sĩ (/admin/chat)"]
            UC_AD_Chat_Sessions(["Xem danh sách các phiên chat của khách"])
            UC_AD_Chat_Reply(["Nhắn tin tư vấn trực tiếp 2 chiều"])
            UC_AD_Chat_ViewImg(["Xem hình ảnh vết thương / triệu chứng thú cưng"])
        end

        subgraph Group_Admin_CMS ["5. Phân rã Quản trị Nội dung & Cấu hình"]
            UC_AD_Dashboard(["Xem Dashboard thống kê Lịch hẹn, Doanh thu, Pet"])
            
            UC_AD_Manage_Service(["Quản lý Dịch vụ khám & Bảng giá"])
            UC_AD_Manage_Branch(["Quản lý Hệ thống chi nhánh & Hotline"])
            UC_AD_Manage_News(["Quản lý Cẩm nang & Tin tức thú y"])
            UC_AD_Manage_Contact(["Xử lý thư liên hệ & Phản ánh của khách"])
            UC_AD_Site_Settings(["Cấu hình phòng khám, Hotline cấp cứu 24/7"])
        end
    end

    Admin --> UC_AD_App_List
    UC_AD_App_List ..->|<< include >>| UC_AD_App_Filter
    UC_AD_App_List ..->|<< extend >>| UC_AD_App_Confirm
    UC_AD_App_List ..->|<< extend >>| UC_AD_App_Cancel
    UC_AD_App_List ..->|<< extend >>| UC_AD_App_Exam
    UC_AD_App_Exam ..->|<< include >>| UC_AD_Diagnosis
    UC_AD_App_Exam ..->|<< include >>| UC_AD_Prescribe
    UC_AD_App_Exam ..->|<< include >>| UC_AD_Complete

    Admin --> UC_AD_View_Followup
    UC_AD_View_Followup ..->|<< include >>| UC_AD_Contact_Cust
    UC_AD_View_Followup ..->|<< extend >>| UC_AD_Confirm_Status
    UC_AD_Confirm_Status ..->|<< include >>| UC_AD_Lock_User
    UC_AD_View_Followup ..->|<< extend >>| UC_AD_Doctor_Note
    UC_AD_View_Followup ..->|<< extend >>| UC_AD_Set_ReExam

    Admin --> UC_AD_Cust_List
    UC_AD_Cust_List ..->|<< include >>| UC_AD_Cust_Detail
    Admin --> UC_AD_Pet_List
    UC_AD_Pet_List ..->|<< extend >>| UC_AD_Pet_Add
    UC_AD_Pet_List ..->|<< extend >>| UC_AD_Pet_Edit
    UC_AD_Pet_List ..->|<< include >>| UC_AD_Pet_History

    Admin --> UC_AD_Chat_Sessions
    UC_AD_Chat_Sessions ..->|<< include >>| UC_AD_Chat_Reply
    UC_AD_Chat_Reply ..->|<< extend >>| UC_AD_Chat_ViewImg

    Admin --> UC_AD_Dashboard
    Admin --> UC_AD_Manage_Service
    Admin --> UC_AD_Manage_Branch
    Admin --> UC_AD_Manage_News
    Admin --> UC_AD_Manage_Contact
    Admin --> UC_AD_Site_Settings
"""

print("Diagrams defined OK!")
