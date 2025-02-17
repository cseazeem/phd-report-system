# 📜 PhD Report Management System

### 🛠️ Tech Stack
- **Backend:** Spring Boot (REST APIs)
- **Database:** PostgreSQL (via Liquibase)
- **Authentication:** JWT (Role-Based Access Control)
- **File Storage:** Local Storage / AWS S3
- **DOCX Processing:** Apache POI
- **PDF Processing:** iText
- **Frontend (if needed):** React / Angular

---

## 📌 Features
✅ **User Authentication & Role-Based Access**  
✅ **OTP-Based User Verification**  
✅ **Admin Approval for New Users**  
✅ **Role Management: PhD Scholar, Coordinator, RAC Members**  
✅ **Report Submission, Review, & Approval Process**  
✅ **Auto-Signature on Final Reports**  
✅ **File Upload & Storage**  
✅ **PDF & DOCX Export**  
✅ **Pagination & Filtering**

---

## 🔄 Workflow
1️⃣ **User Registers (/register)** → OTP Sent via Email.  
2️⃣ **OTP Verification (/verify-otp)** → If correct, user added with `PENDING` status.  
3️⃣ **Admin Approves (/admin/approve/{userId})** → If approved, user assigned role.  
4️⃣ **PhD Scholar Reports**
- Coordinator creates a report.
- RAC Members approve/reject.
- If approved, system adds digital signatures.  
  5️⃣ **Final Report** → Scholar can download & print.

---

## 🚀 Installation Guide

### 🖥️ Clone the Repository
```bash
git clone https://github.com/cseazeem/phd-report-system.git
cd phd-report-system
