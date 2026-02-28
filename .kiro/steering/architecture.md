# Future Architecture & Roadmap

## Phase 1: The NextDNS Bridge (Current)
- **Flow:** Teacher Dashboard -> Quarkus Backend -> NextDNS REST API -> Student iPad.
- **Mechanism:** The iPad is supervised via MDM and follows a locked NextDNS profile. 
- **Teacher Action:** Updating the "Session" in our DB triggers a `PATCH` request to NextDNS to update the profile's allowlist.

## Phase 2: Session Logic
- **Default State:** Whitelist Only (Wikipedia + School Portal).
- **Active State:** Default + Custom Teacher domains + Optional "Open Web" toggle.
- **Joining:** Students scan a QR code which sends their IP/DeviceID to our backend to "link" them to the active session.

## Constraints
- **Data Privacy:** Must comply with Swiss revDSG. Store minimal student data (Device ID only, no names if possible).
- **Hosting:** Prefer Swiss-based VPS or Europe-West6 (Zurich) regions.