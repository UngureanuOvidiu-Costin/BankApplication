import com.luxoft.bankapp.exceptions.ActiveAccountNotSet;
import com.luxoft.bankapp.model.AbstractAccount;
import com.luxoft.bankapp.model.CheckingAccount;
import com.luxoft.bankapp.model.Client;
import com.luxoft.bankapp.model.SavingAccount;
import com.luxoft.bankapp.service.BankReportService;
import com.luxoft.bankapp.service.BankReportServiceImpl;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.BankingImpl;
import com.luxoft.bankapp.model.Client.Gender;
import com.luxoft.bankapp.service.storage.ClientRepository;
import com.luxoft.bankapp.service.storage.MapClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan("com.luxoft.bankapp")
@PropertySource("classpath:clients.properties")
public class BankApplication {

    private static final String[] CLIENT_NAMES =
            {"Jonny Bravo", "Adam Budzinski", "Anna Smith"};

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;


    @Bean(name = "checkingAccount2")
    public CheckingAccount getDemoCheckingAccount2() {
        return new CheckingAccount(1500);
    }

    @Bean(name = "client2")
    public Client getDemoClient2() {
        // Fetch client name from properties file
        String name = environment.getProperty("client2.name");

        // Create a new Client object with the name and gender
        Client client = new Client(name, Gender.MALE);
        client.setCity("Kiev"); // Set the city

        // Retrieve the CheckingAccount bean from the application context
        AbstractAccount checking = (CheckingAccount) applicationContext.getBean("checkingAccount2");

        // Add the account to the client
        client.addAccount(checking);

        return client;
    }

    @Bean(name = "client1")
    public Client getDemoClient1() {
        // Fetch client name from properties file (assuming property key is 'client1.name')
        String name = environment.getProperty("client1.name");

        // Create a new Client object with the name and gender
        Client client = new Client(name, Gender.MALE);
        client.setCity("New York"); // Set the city (example)

        // Retrieve the CheckingAccount bean from the application context
        AbstractAccount checking = (CheckingAccount) applicationContext.getBean("checkingAccount1");

        // Add the account to the client
        client.addAccount(checking);

        // Retrieve another account if needed (e.g., SavingAccount)
        AbstractAccount saving = (SavingAccount) applicationContext.getBean("savingAccount1");
        client.addAccount(saving);

        return client;
    }

    @Bean(name = "savingAccount1")
    public SavingAccount getDemoSavingAccount1() {
        return new SavingAccount(2000); // Example balance for saving account
    }

    @Bean(name = "checkingAccount1")
    public CheckingAccount getDemoCheckingAccount1() {
        return new CheckingAccount(1000); // Example balance for client1
    }

    // Mail v1(inainte sa modific)
//    public static void main(String[] args) {
//
//        ClientRepository repository = new MapClientRepository();
//        Banking banking = initialize(repository);
//
//        workWithExistingClients(banking);
//
//        bankingServiceDemo(banking);
//
////        bankReportsDemo(repository);
//    }

    // Main v2
    public static void main(String[] args) {


        //ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml"); //, "test-clients.xml"
        ApplicationContext context = new AnnotationConfigApplicationContext(BankApplication.class);

        // Get the Banking bean from the context
        Banking banking = initialize(context);

        workWithExistingClients(context);
        bankingServiceDemo(context);

        //bankReportsDemo(repository);
        bankReportsDemo(context);
    }

    public static void bankReportsDemo(ApplicationContext context) {

        System.out.println("\n=== Using BankReportService ===\n");

        // Get the BankReportService bean from the application context
        //BankReportService reportService = (BankReportService) context.getBean("bankReportService");
        BankReportService reportService = context.getBean(BankReportService.class);

        // Now you can use the reportService with already injected repository
        System.out.println("Number of clients: " + reportService.getNumberOfBankClients());

        System.out.println("Number of accounts: " + reportService.getAccountsNumber());

        System.out.println("Bank Credit Sum: " + reportService.getBankCreditSum());
    }

    public static void bankingServiceDemo(ApplicationContext context) {

        System.out.println("\n=== Initialization using Banking implementation ===\n");

        Banking banking = context.getBean(Banking.class);
        Client anna = new Client(CLIENT_NAMES[2], Gender.FEMALE);
        anna = banking.addClient(anna);

        AbstractAccount saving = banking.createAccount(anna, SavingAccount.class);
        saving.deposit(1000);
        banking.updateAccount(anna, saving);

        AbstractAccount checking = banking.createAccount(anna, CheckingAccount.class);
        checking.deposit(3000);
        banking.updateAccount(anna, checking);

        banking.getAllAccounts(anna).stream().forEach(System.out::println);
    }

    public static void workWithExistingClients(ApplicationContext context) {
        System.out.println("\n=======================================");
        System.out.println("\n===== Work with existing clients ======");

        Banking banking = context.getBean(Banking.class);
        Client jonny = banking.getClient(CLIENT_NAMES[0]);

        try {
            jonny.deposit(5_000);
        } catch (ActiveAccountNotSet e) {
            System.out.println(e.getMessage());
            jonny.setDefaultActiveAccountIfNotSet();
            jonny.deposit(5_000);
        }

        System.out.println(jonny);

        Client adam = banking.getClient(CLIENT_NAMES[1]);
        adam.setDefaultActiveAccountIfNotSet();
        adam.withdraw(1500);

        double balance = adam.getBalance();
        System.out.println("\n" + adam.getName() + ", current balance: " + balance);

        banking.transferMoney(jonny, adam, 1000);

        System.out.println("\n=======================================");
        banking.getClients().forEach(System.out::println);
    }

    /*
     * Method that creates a few clients and initializes them with sample values
     */
//    public static Banking initialize(ApplicationContext context) {
//
//        // Get the Banking bean from the context
//        Banking banking = context.getBean(Banking.class);
//
//        // Get the ClientRepository bean from the context
//        ClientRepository repository = context.getBean(ClientRepository.class);
//
//        // Use the repository in the banking service (though, it will already be set via autowiring)
//        banking.setRepository(repository);
//
//        Client client_1 = (Client) context.getBean("client1");
//
//        AbstractAccount savingAccount = new SavingAccount(1000);
//        client_1.addAccount(savingAccount);
////
//        AbstractAccount checkingAccount = new CheckingAccount(1000);
//        client_1.addAccount(checkingAccount);
//
//        Client client_2 = (Client) context.getBean("client2");
//
//        AbstractAccount checking = new CheckingAccount(1500);
//        client_2.addAccount(checking);
//
//        banking.addClient(client_1);
//        banking.addClient(client_2);
//
//        return banking;
//    }

    public static Banking initialize(ApplicationContext context) {
        Banking banking = context.getBean(Banking.class);

        Client client_1 = (Client) context.getBean("client1");
        Client client_2 = (Client) context.getBean("client2");

        banking.addClient(client_1);
        banking.addClient(client_2);

        return banking;
    }
}
