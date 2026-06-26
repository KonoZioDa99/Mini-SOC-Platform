<h1 align="center">Mini-SOC Platform (Log Analysis & Threat Detection)</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Security-JWT_Auth-black?style=for-the-badge" alt="JWT" />
</p>

---

## 📌 Project Overview

The **Mini-SOC Platform** is a custom-built Security Operations Center simulation developed using the Spring Boot framework. The application is designed to ingest raw security logs, parse them into structured data, and identify malicious activities such as brute-force attacks. It features a secure administrative dashboard backed by JWT authentication and a PostgreSQL database for persistent log and alert storage.

**📄 Full Documentation:** [Download the Project Report (PDF)](./Rapport_Mini_SOC.pdf)

---

## 🎯 My Contributions: Log Parsing & Threat Analysis

Within this collaborative project, my primary responsibility was engineering the core **Log Parsing and Threat Enrichment Engine**. This backend component acts as the brain of the alerting system. 

My specific technical contributions included:
* **Threat Classification:** Implemented the algorithmic logic to map specific log patterns and signatures to recognized attack types (e.g., identifying repeated failed login attempts as Brute Force).
* **Dynamic Severity Tagging:** Created the enrichment pipeline that automatically evaluates parsed logs and assigns deterministic severity labels (`Low`, `Medium`, `High`, `Critical`) to generate actionable alerts for the SOC dashboard.

---

## ⚙️ Core System Features

* **Real-Time Log Ingestion:** REST APIs designed to accept incoming log streams from external agents or simulated endpoints.
* **Automated Threat Detection:** Rule-based identification of common attack vectors based on parsed log metrics.
* **Alert Management:** Generation of enriched security alerts containing attack type, timestamp, origin IP, and severity level.
* **Secure Dashboard:** Role-based access control (RBAC) protected by JSON Web Tokens (JWT) ensuring only authorized analysts can view the alert queue.

---

## 🏗️ Technical Architecture & Stack

* **Backend Framework:** Spring Boot (Java)
* **Security:** Spring Security & JWT (JSON Web Tokens)
* **Database:** PostgreSQL (Relational mapping via Spring Data JPA)
* **Architecture Pattern:** MVC (Model-View-Controller) / Service-Oriented Architecture
    * `Controllers`: Handling REST API requests for logs and dashboard data.
    * `Services`: Containing the core business logic (my parsing and severity algorithms).
    * `Repositories`: Database interactions.
    * `Models/Entities`: Data structures representing Logs, Alerts, and Users.

---

## 🚀 Repository Contents

* 📂 `src/main/java/.../`: The complete Spring Boot source code.
    * Pay special attention to the `Service` layer containing the log parsing and enrichment logic.
* 📄 `pom.xml`: Maven dependencies including Spring Web, Spring Security, JWT, and PostgreSQL drivers.
* 📄 `application.properties`: Database connection strings and server configuration (ensure credentials are removed before deploying!).
* 📄 `Rapport_Mini_SOC.pdf`: The detailed project report covering the architecture and implementation phases.
