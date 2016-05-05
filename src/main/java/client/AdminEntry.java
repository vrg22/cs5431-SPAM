
// Representation of single admin within AdminManagementFile
public class AdminEntry extends PasswordStorageEntry {

	public AdminEntry(Admin admin) {
        super(admin);
    }

	public Admin.Header getHeader() {
		return new Admin.Header(Integer.parseInt(get("id")), get("username"));
	}

	public Admin toAdmin() {
		return new Admin(getUsername(), getSalt(), getMaster(),
            getId(), getEncPass(), getRecIV(), getRecovery(),
            getTwoFactorSecret());
	}

}
