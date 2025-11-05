//package ma.pharmachain.init;
//
//import ma.pharmachain.entity.Admin;
//import ma.pharmachain.repository.AdminRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//import ma.pharmachain.config.VaultConfig;
//import ma.pharmachain.entity.Admin;
//import ma.pharmachain.repository.AdminRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//@Component
//public class DefaultAdminInitializer implements CommandLineRunner {
//
//    private final AdminRepository adminRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final VaultConfig vaultConfig;
//
//    public DefaultAdminInitializer(AdminRepository adminRepository,
//                                   PasswordEncoder passwordEncoder,
//                                   VaultConfig vaultConfig) {
//        this.adminRepository = adminRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.vaultConfig = vaultConfig;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        String defaultCode = vaultConfig.getCode();
//
//        // Check if admin already exists
//        if (adminRepository.findByCode(defaultCode).isEmpty()) {
//
//            Admin defaultAdmin = new Admin();
//            defaultAdmin.setUsername(vaultConfig.getUser());
//            defaultAdmin.setPassword(passwordEncoder.encode(vaultConfig.getPass()));
//            defaultAdmin.setCode(defaultCode);
//            defaultAdmin.setIsActive(true);
//            defaultAdmin.setCreatedAt(LocalDateTime.now());
//
//            adminRepository.save(defaultAdmin);
//
//            System.out.println("═══════════════════════════════════════════════════");
//            System.out.println("✅ Default Admin Created Successfully from Vault!");
//            System.out.println("═══════════════════════════════════════════════════");
//            System.out.println("Username: " + vaultConfig.getUser());
//            System.out.println("Code: " + vaultConfig.getCode());
//            System.out.println("Password: (Retrieved securely from Vault)");
//            System.out.println("═══════════════════════════════════════════════════");
//        } else {
//            System.out.println("ℹ️  Default admin already exists in database");
//        }
//    }
//}
