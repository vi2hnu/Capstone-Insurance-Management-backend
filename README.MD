# Insurance Management System - Backend

## Overview

The Insurance Management System is a comprehensive, microservices-based backend solution designed to streamline the operations of insurance providers, hospitals, and policyholders. The system facilitates the entire lifecycle of insurance management, including user registration, policy administration, hospital provider networking, claims processing with multi-stage verification, and financial transaction handling.

Built using Java Spring Boot and containerized with Docker, this project leverages a distributed architecture to ensure scalability, fault tolerance, and ease of maintenance.

## Table of Contents

- [Architecture](#architecture)
- [SonarQube Analysis](#sonarqube-analysis)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Microservices Landscape](#microservices-landscape)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Configuration](#environment-configuration)
  - [Installation & Execution](#installation--execution)
- [API Documentation](#api-documentation)

---

## Architecture

<img width="923" height="869" alt="Image" src="https://github.com/user-attachments/assets/4602639f-2b74-4080-8118-5090fdea5f0b" />

The system follows a Microservices Architecture pattern, utilizing an API Gateway for routing, a Config Server for centralized configuration, and a Service Registry for dynamic service discovery. Asynchronous communication between services is handled via Apache Kafka.

---

## SonarQube Analysis

<img width="1416" height="739" alt="Image" src="https://github.com/user-attachments/assets/a08d6294-cdf8-4b06-bfa1-572aa5806f36" />

<img width="1423" height="765" alt="Image" src="https://github.com/user-attachments/assets/982b9fca-2841-449c-9a6d-2e26c8448a8b" />


(note: you can find the links for sonarqube analysis in REPORT.DOCX)
---

## Key Features

### User Management & Security (Identity Service)
- **Role-Based Access Control (RBAC)**: Distinct roles for Users, Agents, Hospital Providers, Surveyors, and Administrators.
- **Secure Authentication**: JWT (JSON Web Token) based stateless authentication.
- **Multi-Factor Authentication**: Email-based OTP verification for sensitive actions.
- **Profile Management**: Secure handling of user personal and banking data.

### Policy Administration (Policy Service)
- **Policy Creation**: Flexible configuration of insurance policies and coverage details.
- **Plan Management**: Dynamic creation and modification of insurance plans linked to specific policies.
- **Enrollment**: Seamless flow for users to browse and purchase policies.
- **Status Tracking**: Real-time tracking of policy status (Active, Pending, Expired).

### Provider Network (Provider Service)
- **Hospital Registration**: Onboarding workflow for hospitals and healthcare providers.
- **Network Management**: Categorization of providers into distinct networks.
- **Service Linking**: Association of specific insurance plans with eligible hospitals.

### Claims Processing (Claims Service)
- **End-to-End Workflow**: Submission, verification, assessment, and approval/rejection pipeline.
- **Evidence Management**: Integration with cloud storage (Cloudinary) for secure upload of medical documents and media.
- **Surveyor Review**: Dedicated interface for insurance surveyors to validate claim legitimacy.
- **Analytics**: Data aggregation for claim ratios and processing times.

### Billing & Payments (Billing Service)
- **Premium Collection**: Integration with Razorpay for secure premium payments.
- **Payout Automations**: Automated calculation and disbursement of funds to hospitals or users upon claim approval.
- **Transaction History**: Immutable logging of all financial transactions for audit purposes.

### Notification System (Email Service)
- **Event-Driven Messaging**: Asynchronous email delivery triggered by system events (e.g., Registration, Claim Status Change).
- **Templating**: Dynamic email content generation.

---

## Technology Stack

- **Core Framework**: Java 17, Spring Boot 3.x
- **Build Tool**: Maven
- **Databases**:
  - MySQL (Relational Data: Policies, Claims, Providers)
  - MongoDB (Unstructured Data: User Logs, Billing Records)
  - Redis (In memory db used for otp generation and verification with short ttl)
- **Messaging**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config Server
- **Containerization**: Docker, Docker Compose
- **CI/CD**: Jenkins (Groovy Pipeline)
- **Cloud Storage**: Cloudinary (Media Management)
- **Payment Gateway**: Razorpay

---

## Microservices Landscape

| Service Name | Port | Description |
| :--- | :--- | :--- |
| **Api-Gateway** | 9000 | Entry point for all external requests. Handles routing and security filtering. |
| **Config-Server** | 8888 | Centralized configuration for all microservices. |
| **Service-Registry** | 8761 | Eureka server for service discovery and registration. |
| **Identity-Service** | 8081 | Manages authentication, authorization, and user accounts. |
| **Policy-Service** | 8082 | Handles policy definitions, plans, and user subscriptions. |
| **Provider-Service** | 8083 | Manages hospital records and provider networks. |
| **Claims-Service** | 8084 | Processes insurance claims and reviewer workflows. |
| **Billing-Service** | 8085 | Manages payments, payouts, and financial records. |
| **Email-Service** | 8086 | Consumes Kafka events to send email notifications. |

---

## Project Structure

```text
capstone-insurance-management-backend/
├── Api-Gateway/                # Routing and Auth Filters
├── Billing-Service/            # Payment Logic (MongoDB)
├── Claims-Service/             # Claims Logic (MySQL)
├── Config-Server/              # Centralized Config
├── Email-Service/              # Notification Consumer
├── Identity-Service/           # Auth & User Mgmt (MongoDB)
├── Policy-Service/             # Policy Mgmt (MySQL)
├── Provider-Service/           # Hospital Mgmt (MySQL)
├── Service-Registry/           # Eureka Server
├── docker-compose.yaml         # Container orchestration
└── pipeline.groovy             # CI/CD definition
```

---

## Getting Started

### Prerequisites

Docker and Docker Compose are mandatory to run this project. The entire microservices ecosystem is orchestrated via containers.

Ensure the following are installed and running on your machine:
- **Docker Desktop** (or Docker Engine + Docker Compose)
- **Git**

Ensure the jars of each services are created before running


### Environment Configuration

Before executing the system, ensure your environment handles the specific database requirements and port availability.

**1. Database Requirements**
The system connects to a MongoDB instance (managed via Docker). For the services to function correctly, the following logical databases must exist (or will be created upon initialization):
- `userDB` (Used by Identity Service)
- `paymentDB` (Used by Billing Service)

**2. Port Availability**
The following ports **must be free** on your host machine. If these ports are in use, the services will fail to start.

| Service | Port |
| :--- | :--- |
| API Gateway | **9000** |
| Service Registry | **8761** |
| Config Server | **8888** |
| Identity Service | **8081** |
| Policy Service | **8082** |
| Provider Service | **8083** |
| Claims Service | **8084** |
| Billing Service | **8085** |
| Email Service | **8086** |
| Kafka | **9092** |
| MySQL | **3306** |
| MongoDB | **27017** |
| Redis | **6379** |


### Locally without docker
Ensure the following are installed and running on your machine:
- Kafka
- MySql
- redis
- Mongodb

change the config server url in all services to localhost and run all services

---

## API Documentation

Api Documentation [Link](https://docs.google.com/spreadsheets/d/1Y7oRTD3BBCB2Aa30dHHE78SmYxiwv5_V2Q2DcRqtCEk/edit?usp=sharing)