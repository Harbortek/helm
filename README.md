<h1 align="center">
    <img width="64" src="https://raw.githubusercontent.com/Harbortek/helm/refs/heads/develop/docs/images/helm.png"> 
    <br />
    HELM
</h1>

<div align="center">
AI-Powered All-in-One R&D Management Platform

<br />

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](./LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/Harbortek/helm)](https://github.com/Harbortek/helm/releases)
[![Documentation](https://img.shields.io/badge/docs-website-green.svg)](./docs/Index.md)

<br />
<br />

![Plan](/docs/images/project-plan-small.png)![Work Item](/docs/images/tracker-config-small.png)![Document](/docs/images/smart-doc-small.png)![Report](/docs/images/smart-page-small.png)![Work Item Permission](/docs/images/tracker-permission-small.png)

</div>

[English](README.md) | [Chinese](README.CN.md)


## Features
*   Project Management
    *   Project Planning & Scheduling: Create project plans, define project phases, milestones and tasks, and allocate resources and time for tasks. Visualize project progress through Gantt charts and other tools for easy monitoring and adjustment.
    *   Progress Tracking & Monitoring: Real-time tracking of task progress, automatic status updates, early detection of delays and risks, with warning functionality. Visualize project health through dashboards.

*   Requirements Management
    *   Requirements Capture & Organization: Support multiple input methods (text input, document import, etc.) and organize requirements by project structure and functional modules. Handle various requirement types including functional and non-functional requirements.
    *   Requirements Traceability: Establish bidirectional traceability between requirements and other R&D artifacts (design documents, code, test cases, etc.) to track requirement implementation throughout product lifecycle.
    *   Requirements Change Management: Strict control over requirement changes, recording change history, assessing impact, and ensuring all stakeholders are informed.
    *   Requirements Review: Collaborative review environment with commenting, annotation, and approval features.

*   Code Management
    *   Code Repository Integration: Integrate with mainstream code repositories like Git and SVN for effective code management.
    *   Artifact Linkage Management: Establish relationships between code and other R&D artifacts (requirements, test cases, etc.).

*   Test Management
    *   Test Case Management: Create, edit, and manage test cases with version control and reuse capabilities.
    *   Test Planning: Create detailed test plans including scope, strategy, and schedule, aligned with project plans.
    *   Test Execution & Result Recording: Execute test cases, record results, and generate test reports.
    *   Defect Management: Track and manage defects found during testing, linking them to requirements and test cases.

*   Document Management
    *   Document Creation & Editing: Support various document types with rich editing features.
    *   Version Control: Manage document versions with history tracking and comparison.
    *   Approval Workflow: Define document approval processes with online approval support.

*   Configuration Management
    *   Version Control: Manage versions of code, documents, and models with branch and merge support.
    *   Baseline Management: Create and manage project baselines for consistency at specific points.
    *   Configuration Audit: Regular audits to ensure consistency between artifacts and records.

*   Collaboration & Integration
    *   Team Collaboration: Platform for team communication and collaboration with commenting and discussion features.
    *   Third-party Tool Integration: Integrate with development tools like Git, JIRA, and Eclipse for data sharing and process automation.

*   Compliance Management
    *   Standards & Regulations: Support compliance with industry standards like ISO 26262, DO-178C, and ISO 14971.
    *   Compliance Audit: Regular audits for security, privacy, and intellectual property compliance.

## Technical Features
*   Flexible Configuration for Different Business Scenarios
    *   Custom Work Item Types: Customize fields, workflows, permissions, and notifications for different work item types.
    *   Multiple Views: Table, Kanban, Calendar, Document, Matrix views with filtering options.
    *   Modular Project Design: Combine different functional modules like building blocks to adapt to business needs.
*   Ready-to-use Project Templates
    *   Support for Agile, V-Model, ASPICE and other templates, customizable for specific needs.
*   AI+ Smart Documents
    *   Block editing, version control, approval, commenting, and collaboration features.
    *   Import/export Word and ReqIF formats.
    *   AI-assisted document writing.
*   AI+ Smart Reports
    *   Support for various components (tables, charts, dashboards, etc.) for highly customizable reports.
    *   AI-assisted report generation.
*   Multi-level Access Control
    *   Granular permissions for projects, work items, documents, reports, and configurations.

## Technology Stack

-   Frontend: [Vue.js 2](https://vuejs.org/), [Ant Design Vue](https://1x.antdv.com/)
-   Graphics Library: [AntV](https://antv.vision/zh)
-   Backend: [Spring Boot 3](https://spring.io/projects/spring-boot)
-   Database: [MySQL 8](https://www.mysql.com/)
-   Infrastructure: [RAGFlow](https://github.com/infiniflow/ragflow)
-   LLM: [DeepSeek](https://github.com/deepseek-ai/DeepSeek-V3) / [Qwen](https://github.com/QwenLM/Qwen)

## Quick Start
~~~bash
docker run -d -p 8080:8080 harbor.xxx.com/helm/helm:latest
~~~

Open browser and visit http://ip/

## Installation Guide

See [Installation Guide](./docs/Installation_Guide.md)

## User Manual

See [User Manual](./docs/User_Manual.md)

## License
[Apache 2.0](LICENSE)

## Contact Us
[hujun@harbortek.com](hujun@harbortek.com)

