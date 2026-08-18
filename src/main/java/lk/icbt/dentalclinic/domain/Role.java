package lk.icbt.dentalclinic.domain;

/**
 * Staff access roles. RECEPTIONIST can register/search appointments and print bills.
 * ADMIN additionally manages dentists, treatment types and views clinic-wide reports.
 * Assumption: the brief only requires "authorized staff" access; two roles are introduced
 * to demonstrate role-based access control, a common real-world clinic requirement.
 */
public enum Role {
    ADMIN,
    RECEPTIONIST
}
