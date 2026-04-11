# ECSE321 Fashion store project: Team 18

Welcome to Team 18's fashion store project for ECSE 321 at McGill University! This project was built by Cyrus Fung, Qiuyu Huang, Flavie Qin, Kenneth Wang, Carolyn Wu, Jennifer You, and Aurore Zhang, with each person's contributions described [below](https://github.com/McGill-ECSE321-W26/ecse321-project-18?tab=readme-ov-file#team-introduction).

In addition to this README, we have also compiled a [comprehensive wiki thoroughly documenting our development process and how the application works](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki) that we invite you to explore.

## Table of contents

- [Project overview](#project-overview)
- [Quickstart](#quickstart)
  - [Database setup](#database-setup)
  - [Run the backend](#run-the-backend)
    - [Method 1](#method-1)
    - [Method 2](#method-2)
  - [Run the frontend](#run-the-frontend)
- [Team introduction](#team-introduction)
- [Progress timeline](#progress-timeline)
  - [Deliverable 1](#deliverable-1)
  - [Deliverable 2](#deliverable-2)
  - [Deliverable 3](#deliverable-3)

---

## Project overview

This group project, associated with ECSE 321 (Introduction to Software Engineering) at McGill University, consists of developing an online platform for a local fashion store that allows for ordering and delivering clothes. [Learn more about our team in "Team introduction"!](#team-introduction)

The system being developed supports multiple user roles, including customers, employees, and one manager, each with role-specific access:

- **Customers** can browse the product catalog, manage their shopping cart, and place orders
- **Employees** prepare customer orders
- The **manager** oversees the delivery preparations and maintains the product catalog

The web application has a React frontend, a Spring Boot backend (Java), and a PostgreSQL database.

**For more information about the platform and/or the development process, please see ["Progress timeline"](#progress-timeline) below, and [consult the wiki](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki).**

---

## Quickstart

This section introduces how to run our web application in production mode. We recommend completing the sections in their order of appearance, starting from the [first subsection (Database setup)](#database-setup).

> See also [Quickstart in the wiki](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Quickstart) and other wiki documentation for more details.
>
> For a more in-depth guide on how to develop, build, and run our project, please consult the [Development guide](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Development-guide). Our backend includes a (developer mode only) endpoint that populates the database with several accounts, clothing products, and more, that may be useful!

### Database setup

Follow the [tutorial instructions](https://mcgill-ecse321-w26.github.io/ECSE_321_Tut_Notes_W2026/#_setting_up_a_local_postgresql_database) to set up a local PostgreSQL database.

The backend server expects the following credentials:

- Database name: `fashion_store`
- Username: `postgres`
- Password: `fashionstore`
- Port: `localhost:5432`

---

### Run the backend

**Make sure to have the database up and running at the same time!**  
There are several ways to get the backend running. **The default backend port is port `8080`**, i.e. `http://localhost:8080`.

#### Method 1

1. Ensure you're in the `backend` directory, then run: `./gradlew bootJar -xtest`
2. Change into the directory: `backend/build/libs`
3. Run: `java -jar fashionstore-0.0.1-SNAPSHOT.jar`

#### Method 2

1. Run the Gradle `bootRun` task through:
   1. The Gradle wrapper, or your local Gradle installation (ensure you're in the `backend` directory): `./gradlew bootRun -x test`
   2. IntelliJ (or the IDE that you are using).

---

### Run the frontend

**Make sure to have both the database and backend up and running at the same time!** The frontend makes HTTP requests to the backend, and these will fail if the backend (which interacts with the database) is not actively running.

1. If you haven't already, go into the `frontend` directory and install all `npm` dependencies: `cd frontend && npm install`
2. To start a production server, run the following command (make sure you're in the `frontend` directory): `npm run start`
3. Go to `localhost:3000` in your browser to see the web page!

Note that the backend automatically creates a **manager account with the following credentials**, which you may use to login (customer and employee accounts [can be created on the Register page](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/User-guide#non-authenticated-routes)):

- Email: `admin@stilton.com`
- Password: `security`

---

## Team introduction

The team members of our group all share a similar background, as Marianopolis College graduates and current Software Engineering students at McGill University. We also collaborated together last semester on the development a cheese manager application, and have come together once again to develop another masterpiece :D

Each team member and their role are presented in the table below.

| Name         | GitHub username | Role               | Responsibilities                                                                                                                                                                                                                                                                                                                                     |
| ------------ | --------------- | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Cyrus Fung   | cfung89         | Software lead      | Implement and manage the source code version control and build systems. **In consultation with the project manager:** specify task breakdown for software in consultation with the project manager, ensure software requirements are met. **In consultation with the documentation lead:** define the documentation structures to be used.           |
| Qiuyu Huang  | redacted24      | Testing lead       | Supervise implementation of testing at all levels (unit, integration, and so on). **In consultation with the project manager:** define testing strategies, ensure testing requirements are met. **In consultation with the documentation lead:** define the testing document formats to be used.                                                     |
| Flavie Qin   | flavieq88       | Project manager    | Allocate main responsibilities for each task in each deliverable, resolve disputes and make primary decisions based on discussions, ensure the completion and submission of deliverables on time, ensure all requirements are well met, oversee management of the GitHub project board. Consult with the software, testing, and documentation leads. |
| Kenneth Wang | KennethWang6    | Software developer | Work under the software lead to conceptualize frontend and backend components that meet requirements, assist the software lead in task definition and scheduling, implement the software, document the software work done.                                                                                                                           |
| Carolyn Wu   | cw118           | Documentation lead | Track all documentation in the system, allocate documentation tasks to team members in consultation with the project manager, specify and implement format for all documents, manage the project wiki, manage GitHub issues and milestones in consultation with the project manager.                                                                 |
| Jennifer You | jenni4u         | Software developer | Work under the software lead to conceptualize frontend and backend components that meet requirements, assist the software lead in task definition and scheduling, implement the software, document the software work done.                                                                                                                           |
| Aurore Zhang | ororio0         | Software developer | Work under the software lead to conceptualize frontend and backend components that meet requirements, assist the software lead in task definition and scheduling, implement the software, document the software work done.                                                                                                                           |

---

## Progress timeline

Here is an overview of our team's progress and contributions across all deliverables/sprints (see also individual breakdowns below):

| Name         | Role               | [Deliverable 1](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Project-report:-deliverable-1) | [Deliverable 2](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Project-report:-deliverable-2) | [Deliverable 3](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Project-report:-deliverable-3) | Total |
| ------------ | ------------------ | ------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------ | ----- |
| Cyrus Fung   | Software lead      | 28                                                                                                           | 40                                                                                                           | 67                                                                                                           | 135   |
| Qiuyu Huang  | Testing lead       | 15                                                                                                           | 25                                                                                                           | 32                                                                                                           | 72    |
| Flavie Qin   | Project manager    | 25                                                                                                           | 37                                                                                                           | 44                                                                                                           | 106   |
| Kenneth Wang | Software developer | 17                                                                                                           | 28                                                                                                           | 34                                                                                                           | 79    |
| Carolyn Wu   | Documentation lead | 23                                                                                                           | 32                                                                                                           | 81                                                                                                           | 136   |
| Jennifer You | Software developer | 19                                                                                                           | 27                                                                                                           | 49                                                                                                           | 95    |
| Aurore Zhang | Software developer | 17                                                                                                           | 27                                                                                                           | 35                                                                                                           | 79    |

---

### Deliverable 1

**See also our [deliverable/sprint 1 report](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Project-report:-deliverable-1) in the wiki.**

| Name         | Contributions                                                                                                                                                                                                                               | Hours |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| Cyrus Fung   | Domain model, Umple code generation, Gradle (Checkstyle, PMD, Spotbugs), Spring Boot and PostgreSQL setup, requirements, GitHub Actions, persistence layer, documentation, detailed use case specification, E-IDEA workshop and assignments | 28    |
| Qiuyu Huang  | Requirements, detailed use case specification, persistence layer testing, E-IDEA workshop and assignments                                                                                                                                   | 15    |
| Flavie Qin   | Requirements, domain model, use case diagrams, detailed use case specifications, persistence layer testing, documentation, E-IDEA workshop and assignments                                                                                  | 25    |
| Kenneth Wang | Requirements, detailed use case specification, persistence layer testing, README, E-IDEA workshop and assignments                                                                                                                           | 17    |
| Carolyn Wu   | GitHub project setup (issues, milestones, Kanban board), wiki and README, meeting minutes, requirements, detailed use case specification, documentation, GitHub Actions, persistence layer testing, E-IDEA workshop and assignments         | 23    |
| Jennifer You | Requirements, detailed use case specification, meeting minutes, persistence layer testing, E-IDEA workshop and assignments                                                                                                                  | 19    |
| Aurore Zhang | Requirements, detailed use case specification, persistence layer testing, E-IDEA workshop and assignments                                                                                                                                   | 17    |

---

### Deliverable 2

**See also our [deliverable/sprint 2 report](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Project-report:-deliverable-2) in the wiki.**

| Name         | Contributions                                                                                                                                                                                                        | Hours |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| Cyrus Fung   | Domain model fix, DTO implementations, build system, API endpoint definition, service and controller methods, service unit tests, backend integration tests                                                          | 40    |
| Qiuyu Huang  | Service and controller methods, API endpoint definition, service unit tests, backend integration tests, documentation                                                                                                | 25    |
| Flavie Qin   | Domain model fix, API endpoint definition, service and controller methods, service unit tests, backend integration tests, documentation                                                                              | 37    |
| Kenneth Wang | API endpoint definition, service and controller methods, service unit tests, backend integration tests, project report and wiki, documentation                                                                       | 28    |
| Carolyn Wu   | API endpoint definition, missing repository methods, service and controller methods, repository and service unit tests, backend integration tests, GitHub project and issues, project report and wiki, documentation | 32    |
| Jennifer You | API endpoint definition, service and controller methods, service unit tests, backend integration tests, documentation                                                                                                | 27    |
| Aurore Zhang | API endpoint definition, service and controller methods, service unit tests, backend integration tests, documentation                                                                                                | 27    |

---

### Deliverable 3

See also our [deliverable/sprint 3 report](https://github.com/McGill-ECSE321-W26/ecse321-project-18/wiki/Project-report:-deliverable-3) in the wiki.

| Name         | Contributions                                                                                                                                                                                                       | Hours |
| :----------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | :---- |
| Cyrus Fung   | Cart page, View accounts page, UI/UX styling, update backend (add endpoints, tests), backend developer mode, test/demo data generation, project report and wiki, documentation                                      | 67    |
| Qiuyu Huang  | Home page, 404 page, Edit product page, UI/UX, documentation                                                                                                                                                        | 32    |
| Flavie Qin   | Cart page (place order modal), Order history page, Manage orders (manager) page, Manage orders (employee) page, update backend, architecture model, logo, project wiki, documentation                               | 44    |
| Kenneth Wang | Manage products page, Edit product page, update backend, documentation                                                                                                                                              | 34    |
| Carolyn Wu   | Frontend setup (`npm`, React/TanStack, Tailwind/HeroUI), Login page, Register page, Shop/Products page, Edit product page, UI/UX, update backend, GitHub project and issues, project report and wiki, documentation | 81    |
| Jennifer You | Manage products page, Edit product page, View account details (manager) page, UI/UX, update backend, documentation                                                                                                  | 49    |
| Aurore Zhang | Manage orders (manager) page, Manage orders (employee) page, Dashboard (manager) page, Orders page, My Account page, update backend, documentation                                                                  | 35    |
