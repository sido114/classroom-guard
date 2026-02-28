# Requirements Document

## Introduction

This document defines the requirements for the Classroom URL Management feature in Classroom Guard. This feature enables teachers to create classrooms and add URLs to each classroom for future DNS whitelist/blacklist control. This is a minimal viable feature focused on core "add-only" functionality. Edit and delete operations will be added in future iterations. The feature is designed for non-technical teachers who need a simple, intuitive interface to control internet access during lessons.

## Glossary

- **Teacher**: The primary user who creates and manages classrooms and URL lists
- **Classroom**: A logical grouping representing a physical class or lesson group, containing a list of URLs
- **URL**: A web address (domain or full URL) that will be allowed or blocked for students in a classroom
- **URL_Manager**: The backend system component that handles classroom and URL operations
- **Frontend**: The Next.js web application that teachers interact with
- **DNS_Filter**: The future NextDNS integration that will enforce URL policies (not implemented in this spec)
- **Session**: A future concept where students join a classroom via QR code to activate filtering

## Requirements

### Requirement 1: Create Classroom

**User Story:** As a teacher, I want to create a new classroom with a name and optional description, so that I can organize my URL lists by class or subject.

#### Acceptance Criteria

1. THE Frontend SHALL display a form to create a new classroom with name and description fields
2. WHEN a teacher submits a valid classroom name, THE URL_Manager SHALL create a classroom record in the database
3. THE URL_Manager SHALL validate that the classroom name is not empty and does not exceed 100 characters
4. IF the classroom name is empty or exceeds 100 characters, THEN THE URL_Manager SHALL return a 400 error with a descriptive message
5. WHEN a classroom is created, THE URL_Manager SHALL assign a unique identifier and timestamp
6. THE Frontend SHALL display the newly created classroom in the classroom list immediately after creation

### Requirement 2: List Classrooms

**User Story:** As a teacher, I want to see all my classrooms in a list, so that I can select which classroom to manage.

#### Acceptance Criteria

1. THE Frontend SHALL display all classrooms in a visually organized list or grid
2. WHEN the teacher views the classroom list, THE URL_Manager SHALL return all classrooms ordered by creation date (newest first)
3. THE Frontend SHALL display the classroom name, description, URL count, and creation date for each classroom
4. WHEN no classrooms exist, THE Frontend SHALL display a helpful message encouraging the teacher to create their first classroom
5. THE Frontend SHALL provide a clear visual distinction between different classrooms

### Requirement 3: View Classroom Details

**User Story:** As a teacher, I want to view the details of a specific classroom including all its URLs, so that I can see what websites are configured for that class.

#### Acceptance Criteria

1. WHEN a teacher selects a classroom, THE Frontend SHALL display the classroom details page
2. THE URL_Manager SHALL return the classroom information including name, description, and all associated URLs
3. THE Frontend SHALL display all URLs associated with the classroom in a list format
4. WHEN a classroom has no URLs, THE Frontend SHALL display a message indicating the classroom is empty
5. THE Frontend SHALL provide navigation to return to the classroom list

### Requirement 4: Add URL to Classroom

**User Story:** As a teacher, I want to add URLs to a classroom, so that I can build a whitelist of allowed websites for my students.

#### Acceptance Criteria

1. WHEN viewing a classroom, THE Frontend SHALL display a form to add a new URL
2. THE URL_Manager SHALL validate that the URL is not empty and is a valid domain or URL format
3. WHEN a valid URL is submitted, THE URL_Manager SHALL associate the URL with the classroom
4. IF the URL is empty or invalid, THEN THE URL_Manager SHALL return a 400 error with a descriptive message
5. THE URL_Manager SHALL prevent duplicate URLs within the same classroom
6. IF a duplicate URL is submitted, THEN THE URL_Manager SHALL return a 400 error indicating the URL already exists
7. WHEN a URL is added, THE Frontend SHALL update the URL list immediately without requiring a page refresh
8. THE URL_Manager SHALL store the URL with a timestamp indicating when it was added

### Requirement 5: Teacher-Friendly UI Design

**User Story:** As a teacher who is not a tech expert, I want a clean and intuitive interface, so that I can manage classrooms without confusion.

#### Acceptance Criteria

1. THE Frontend SHALL use clear, non-technical language in all labels and messages
2. THE Frontend SHALL provide visual feedback for all user actions (loading states, success messages, error messages)
3. THE Frontend SHALL use a consistent color scheme and typography throughout the application
4. THE Frontend SHALL be responsive and work well on desktop, tablet, and mobile devices
5. THE Frontend SHALL use icons alongside text to improve visual clarity
6. THE Frontend SHALL group related actions together (e.g., all classroom management actions in one area)

### Requirement 6: URL Format Flexibility

**User Story:** As a teacher, I want to add URLs in various formats (domain only, with protocol, with path), so that I don't have to worry about exact formatting.

#### Acceptance Criteria

1. THE URL_Manager SHALL accept URLs in multiple formats: "example.com", "https://example.com", "https://example.com/path"
2. THE URL_Manager SHALL normalize URLs to a consistent format for storage
3. THE Frontend SHALL display URLs in a user-friendly format
4. THE URL_Manager SHALL validate that the URL contains at least a valid domain name
5. IF the URL format is completely invalid, THEN THE URL_Manager SHALL return a 400 error with guidance on acceptable formats

### Requirement 7: Future DNS Integration Preparation

**User Story:** As a system architect, I want the classroom and URL data structure to support future NextDNS integration, so that we can implement DNS filtering without major refactoring.

#### Acceptance Criteria

1. THE URL_Manager SHALL store URLs in a format compatible with DNS filtering (domain-based)
2. THE URL_Manager SHALL maintain the relationship between classrooms and URLs in a way that supports future session-based filtering
3. THE URL_Manager SHALL store metadata (timestamps, creation info) that will be useful for future audit and compliance requirements
4. THE URL_Manager SHALL design the database schema to allow future addition of URL type (whitelist/blacklist) without migration
5. THE URL_Manager SHALL ensure classroom identifiers can be used as references in future session management

### Requirement 8: Data Persistence and Reliability

**User Story:** As a teacher, I want my classroom and URL data to be saved reliably, so that I don't lose my work.

#### Acceptance Criteria

1. THE URL_Manager SHALL persist all classroom and URL data to the PostgreSQL database
2. WHEN a database operation fails, THE URL_Manager SHALL return a 500 error with a generic error message
3. THE URL_Manager SHALL use transactions to ensure data consistency when creating or deleting classrooms with URLs
4. THE URL_Manager SHALL log all database errors for debugging purposes
5. THE Frontend SHALL display user-friendly error messages when operations fail


