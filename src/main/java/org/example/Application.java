package org.example;

import java.util.List;
import java.util.Scanner;
import org.example.clients.ApiClient;
import org.example.dto.CandidateRequest;
import org.example.dto.RoleResponse;
import org.example.dto.StatusRequest;
import org.example.encoders.Encoder;
import org.example.exception.ApiErrorException;
import org.example.validators.EmailValidator;
import org.springframework.web.reactive.function.client.WebClient;
import static org.example.utils.ConsoleMessages.AVAILABLE_ROLES;
import static org.example.utils.ConsoleMessages.INPUT_CORRECT_EMAIL;
import static org.example.utils.ConsoleMessages.INPUT_CORRECT_ROLE_NUMBER;
import static org.example.utils.ConsoleMessages.INPUT_EMAIL;
import static org.example.utils.ConsoleMessages.INPUT_FIRST_NAME;
import static org.example.utils.ConsoleMessages.INPUT_LAST_NAME;
import static org.example.utils.ConsoleMessages.INPUT_ROLE_NUMBER;
import static org.example.utils.ConsoleMessages.REGISTRATION;
import static org.example.models.Status.INCREASED;

public class Application {
    private final ApiClient apiClient;

    public Application() {
        this.apiClient = new ApiClient(WebClient.builder(), null);
    }

    public Application(String baseUrl) {
        this.apiClient = new ApiClient(WebClient.builder(), baseUrl);
    }

    public Application(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public void execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.print(INPUT_LAST_NAME);
        String lastName = scanner.nextLine();

        System.out.print(INPUT_FIRST_NAME);
        String firstName = scanner.nextLine();

        System.out.print(INPUT_EMAIL);
        String email = scanner.nextLine();
        while (!EmailValidator.isValidEmail(email)) {
            System.out.print(INPUT_CORRECT_EMAIL);
            email = scanner.nextLine();
        }

        List<String> roles = getRolesList();

        System.out.println(AVAILABLE_ROLES);
        for (int i = 0; i < roles.size(); i++) {
            System.out.println(i + 1 + ". " + roles.get(i));
        }

        System.out.print(INPUT_ROLE_NUMBER);
        int roleIndex = scanner.nextInt() - 1;
        while (roleIndex > roles.size()) {
            System.out.print(INPUT_CORRECT_ROLE_NUMBER);
            roleIndex = scanner.nextInt() - 1;
        }
        String selectedRole = roles.get(roleIndex);
        CandidateRequest candidate = new CandidateRequest(lastName, firstName, email, selectedRole);

        System.out.printf((REGISTRATION) + "%n", lastName, firstName, email, selectedRole);

        String result = register(candidate);
        System.out.println(result);

        scanner.close();
    }

    private List<String> getRolesList() {
        RoleResponse response = apiClient.getRoles();
        return response.roles();
    }

    String register(CandidateRequest candidate) {
        try {
            apiClient.signUp(candidate);
            String code = apiClient.getCode(candidate.email());
            String token = Encoder.encodeToBase64(candidate.email(), code);
            StatusRequest status = new StatusRequest(token, INCREASED.getStatus());
            return apiClient.setStatus(status);
        } catch (ApiErrorException exception) {
            return exception.getMessage();
        }
    }
}
