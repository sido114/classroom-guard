# Product Vision: Classroom Guard (Swiss EdTech)

## Purpose
A classroom management tool for Swiss schools that allows teachers to control iPad internet access in real-time. It bridges the gap between static school filters (Swisscom) and the dynamic needs of a 45-minute lesson.

## Target User: The Teacher
- Needs a "Magic Button" to lock/unlock the web.
- Is NOT a tech expert; the UI must be dead simple.
- Uses a QR code to let students join a "Focus Session."

## Future "Big Picture"
1. **Pilot Phase:** Use NextDNS API as the "engine" to skip the Apple Network Extension complexity.
2. **Growth Phase:** Partner with Swiss MSPs (Managed Service Providers) to deploy via MDM.
3. **Maturity Phase:** Replace NextDNS with a custom Kotlin-based DNS engine for full control.