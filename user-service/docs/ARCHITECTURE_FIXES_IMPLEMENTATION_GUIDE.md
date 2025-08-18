# 🛠️ Guia de Implementação - Correção de Violações Arquiteturais

**Data:** 18 de agosto de 2025  
**Projeto:** User Service - ClinicBoard  
**Objetivo:** Guia prático para correção das violações detectadas pelos Fitness Functions

---

## 📋 **CHECKLIST DE CORREÇÕES**

### 🔥 **FASE 1: CORREÇÕES CRÍTICAS (SPRINT ATUAL)**

#### **1. Purificar Domain - Remover Spring Security**

**Status:** ❌ **PENDENTE**  
**Prioridade:** 🔥 **CRÍTICA**  
**Estimativa:** 4 horas

**Passos:**

1. **Criar UserDetailsAdapter na infraestrutura**
```java
// src/infrastructure/adapter/out/authentication/UserDetailsAdapter.java
@Component
public class UserDetailsAdapter implements UserDetails {
    private final User user;
    
    public UserDetailsAdapter(User user) {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return user.getPassword().getValue();
    }
    
    @Override
    public String getUsername() {
        return user.getEmail().getValue();
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return user.isActive(); }
}
```

2. **Limpar User.java - Remover UserDetails**
```java
// src/domain/model/User.java - REMOVER implements UserDetails
@Entity
public class User {
    private final UserId id;
    private final UserName name;
    private final Email email;
    private final Password password;
    private final UserRole role;
    private final ContactDetails contact;
    private final boolean active;
    
    // REMOVER todos os métodos do UserDetails
    // ADICIONAR comportamentos de negócio
    
    public Set<String> getRoles() {
        return Set.of("USER", role.getValue());
    }
    
    public boolean hasRole(String requiredRole) {
        return getRoles().contains(requiredRole);
    }
    
    public boolean isActive() {
        return active;
    }
}
```

3. **Atualizar UserDetailsService**
```java
// src/infrastructure/adapter/out/authentication/UserDetailsServiceAdapter.java
@Service
public class UserDetailsServiceAdapter implements UserDetailsService {
    private final UserRepositoryPort userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(new Email(email))
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        return new UserDetailsAdapter(user);
    }
}
```

**Validação:**
```bash
mvn test -Dtest=AntiPatternsFitnessTest#domainShouldNotDependOnManyExternalPackages
```

---

#### **2. Tornar Domain Rico - Eliminar Modelo Anêmico**

**Status:** ❌ **PENDENTE**  
**Prioridade:** 🔥 **CRÍTICA**  
**Estimativa:** 6 horas

**Passos:**

1. **Tornar campos final em User.java**
```java
// src/domain/model/User.java
@Entity
public class User {
    private final UserId id;
    private final UserName name;
    private final Email email;
    private Password password; // Pode mudar com changePassword()
    private final UserRole role;
    private ContactDetails contact; // Pode mudar com updateContact()
    private final boolean active;
    
    // Constructor com todos os campos obrigatórios
    public User(UserId id, UserName name, Email email, Password password, 
                UserRole role, ContactDetails contact) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.email = requireNonNull(email);
        this.password = requireNonNull(password);
        this.role = requireNonNull(role);
        this.contact = requireNonNull(contact);
        this.active = true;
    }
}
```

2. **Adicionar comportamentos de negócio**
```java
// src/domain/model/User.java - ADICIONAR
public class User {
    
    public void changePassword(Password newPassword, PasswordPolicy policy) {
        if (!policy.isValid(newPassword)) {
            throw new BusinessException("Password não atende aos requisitos de política");
        }
        
        if (password.equals(newPassword)) {
            throw new BusinessException("Nova senha deve ser diferente da atual");
        }
        
        this.password = newPassword;
        // TODO: Publicar PasswordChangedEvent
    }
    
    public void updateContact(ContactDetails newContact) {
        if (!newContact.isValid()) {
            throw new BusinessException("Informações de contato inválidas");
        }
        
        this.contact = newContact;
        // TODO: Publicar ContactUpdatedEvent
    }
    
    public boolean canAccessResource(String resourceType) {
        return role.hasPermissionFor(resourceType);
    }
    
    public void activate() {
        if (active) {
            throw new BusinessException("Usuário já está ativo");
        }
        this.active = true;
    }
    
    public void deactivate() {
        if (!active) {
            throw new BusinessException("Usuário já está inativo");
        }
        this.active = false;
    }
}
```

3. **Criar PasswordPolicy**
```java
// src/domain/service/PasswordPolicy.java
@Component
public class PasswordPolicy {
    private static final int MIN_LENGTH = 8;
    private static final String SPECIAL_CHARS = "!@#$%^&*()";
    
    public boolean isValid(Password password) {
        String value = password.getValue();
        
        return value.length() >= MIN_LENGTH &&
               containsUpperCase(value) &&
               containsLowerCase(value) &&
               containsDigit(value) &&
               containsSpecialChar(value);
    }
    
    public void validate(Password password) {
        if (!isValid(password)) {
            throw new BusinessException("Password deve ter pelo menos 8 caracteres, " +
                "incluindo maiúscula, minúscula, número e caractere especial");
        }
    }
    
    private boolean containsUpperCase(String value) {
        return value.chars().anyMatch(Character::isUpperCase);
    }
    
    private boolean containsLowerCase(String value) {
        return value.chars().anyMatch(Character::isLowerCase);
    }
    
    private boolean containsDigit(String value) {
        return value.chars().anyMatch(Character::isDigit);
    }
    
    private boolean containsSpecialChar(String value) {
        return value.chars().anyMatch(c -> SPECIAL_CHARS.indexOf(c) >= 0);
    }
}
```

**Validação:**
```bash
mvn test -Dtest=AntiPatternsFitnessTest#domainModelsShouldHaveBehaviors
```

---

#### **3. Quebrar Dependência Cíclica**

**Status:** ❌ **PENDENTE**  
**Prioridade:** 🔥 **CRÍTICA**  
**Estimativa:** 3 horas

**Passos:**

1. **Criar nova estrutura de packages**
```
src/infrastructure/
├── adapter/
│   ├── in/
│   │   └── web/
│   └── out/
│       ├── persistence/
│       └── authentication/
├── config/
│   └── BeanConfiguration.java
└── security/              // <- NOVO
    ├── TokenService.java
    ├── JwtTokenProvider.java
    └── SecurityConfig.java
```

2. **Mover TokenService**
```bash
# Mover arquivo
mv src/infrastructure/config/TokenService.java src/infrastructure/security/
```

3. **Atualizar imports**
```java
// src/infrastructure/adapter/out/authentication/AuthenticationAdapter.java
import com.clinicboard.user_service.infrastructure.security.TokenService; // NOVO

// src/infrastructure/config/BeanConfiguration.java
import com.clinicboard.user_service.infrastructure.security.TokenService; // NOVO
```

4. **Revisar BeanConfiguration**
```java
// src/infrastructure/config/BeanConfiguration.java
@Configuration
public class BeanConfiguration {
    
    @Bean
    public UserRepositoryPort userRepositoryPort(UserPersistenceAdapter adapter) {
        return adapter; // Sem ciclo
    }
    
    @Bean
    public AuthenticationServicePort authenticationServicePort(
            AuthenticationManager authManager,
            TokenService tokenService, // Injetado automaticamente
            UserRepositoryPort userRepository) {
        return new AuthenticationAdapter(authManager, tokenService, userRepository);
    }
}
```

**Validação:**
```bash
mvn test -Dtest=AntiPatternsFitnessTest#detectCircularDependenciesInInfrastructure
```

---

### ⚠️ **FASE 2: MELHORIAS ARQUITETURAIS (PRÓXIMO SPRINT)**

#### **4. Implementar Value Objects**

**Status:** ❌ **PENDENTE**  
**Prioridade:** ⚠️ **ALTA**  
**Estimativa:** 8 horas

**Passos:**

1. **Criar UserName Value Object**
```java
// src/domain/model/UserName.java
public record UserName(String value) {
    public UserName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode estar vazio");
        }
        
        String trimmed = value.trim();
        if (trimmed.length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
        
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Nome não pode exceder 100 caracteres");
        }
        
        if (!trimmed.matches("^[a-zA-ZÀ-ÿ\\s'\\-]+$")) {
            throw new IllegalArgumentException("Nome contém caracteres inválidos");
        }
        
        // Normalizar
        value = trimmed.replaceAll("\\s+", " ");
    }
    
    public String getFirstName() {
        return value.split(" ")[0];
    }
    
    public String getLastName() {
        String[] parts = value.split(" ");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
    
    public String getInitials() {
        return Arrays.stream(value.split(" "))
            .map(part -> part.substring(0, 1).toUpperCase())
            .collect(Collectors.joining());
    }
}
```

2. **Criar UserId Value Object**
```java
// src/domain/model/UserId.java
public record UserId(String value) {
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    );
    
    public UserId {
        if (value == null || !UUID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("ID deve ser um UUID válido");
        }
    }
    
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
    
    public static UserId fromString(String value) {
        return new UserId(value);
    }
}
```

3. **Criar Email Value Object**
```java
// src/domain/model/Email.java
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode estar vazio");
        }
        
        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        
        value = normalized;
    }
    
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }
}
```

4. **Criar Password Value Object**
```java
// src/domain/model/Password.java
public record Password(String value) {
    public Password {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password não pode estar vazio");
        }
        // Password já deve vir encriptado do UseCase
    }
    
    public static Password fromPlainText(String plainText) {
        if (plainText == null || plainText.length() < 8) {
            throw new IllegalArgumentException("Password deve ter pelo menos 8 caracteres");
        }
        // Este método só deve ser usado em testes ou migrations
        return new Password(plainText);
    }
    
    public static Password fromEncrypted(String encryptedValue) {
        return new Password(encryptedValue);
    }
}
```

5. **Atualizar User.java**
```java
// src/domain/model/User.java
@Entity
public class User {
    private final UserId id;
    private final UserName name;      // ← Value Object
    private final Email email;        // ← Value Object
    private Password password;        // ← Value Object
    private final UserRole role;
    private ContactDetails contact;
    private final boolean active;
    
    // Atualizar constructor e métodos
}
```

**Validação:**
```bash
mvn test -Dtest=AntiPatternsFitnessTest#domainShouldUseValueObjectsInsteadOfPrimitives
```

---

#### **5. Purificar Application Layer**

**Status:** ❌ **PENDENTE**  
**Prioridade:** ⚠️ **ALTA**  
**Estimativa:** 4 horas

**Passos:**

1. **Criar PasswordEncoderPort**
```java
// src/application/port/out/PasswordEncoderPort.java
public interface PasswordEncoderPort {
    String encode(String plainPassword);
    boolean matches(String plainPassword, String encodedPassword);
}
```

2. **Implementar Adapter**
```java
// src/infrastructure/adapter/out/security/PasswordEncoderAdapter.java
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    private final BCryptPasswordEncoder encoder;
    
    public PasswordEncoderAdapter() {
        this.encoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        return encoder.matches(plainPassword, encodedPassword);
    }
}
```

3. **Atualizar UseCase**
```java
// src/application/usecase/CreateUserUseCaseImpl.java
@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder; // ← Port!
    
    public CreateUserUseCaseImpl(UserRepositoryPort userRepository, 
                                PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // ← Injeção da abstração
    }
    
    @Override
    public User createUser(CreateUserCommand command) {
        // Usar Port ao invés de implementação direta
        String encodedPassword = passwordEncoder.encode(command.password());
        
        User user = new User(
            UserId.generate(),
            new UserName(command.name()),
            new Email(command.email()),
            Password.fromEncrypted(encodedPassword),
            new UserRole(command.role()),
            new ContactDetails(command.phone(), command.address())
        );
        
        return userRepository.save(user);
    }
}
```

4. **Configurar Bean**
```java
// src/infrastructure/config/BeanConfiguration.java
@Bean
public PasswordEncoderPort passwordEncoderPort() {
    return new PasswordEncoderAdapter();
}
```

**Validação:**
```bash
mvn test -Dtest=MessagingArchitectureFitnessTest#applicationMustUsePortsForEventPublishing
```

---

#### **6. Corrigir Linguagem Ubíqua**

**Status:** ❌ **PENDENTE**  
**Prioridade:** ⚠️ **ALTA**  
**Estimativa:** 2 horas

**Passos:**

1. **Renomear ContactInfo**
```bash
# Refactoring seguro no IDE
mv src/domain/model/ContactInfo.java src/domain/model/ContactDetails.java
```

2. **Atualizar classe**
```java
// src/domain/model/ContactDetails.java
public record ContactDetails(PhoneNumber phone, Address address) {
    public ContactDetails {
        requireNonNull(phone, "Telefone é obrigatório");
        requireNonNull(address, "Endereço é obrigatório");
    }
    
    public boolean isValid() {
        return phone.isValid() && address.isValid();
    }
    
    public boolean hasCompleteInformation() {
        return phone.isValid() && address.isComplete();
    }
}
```

3. **Criar Value Objects de suporte**
```java
// src/domain/model/PhoneNumber.java
public record PhoneNumber(String value) {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$" // Formato internacional simplificado
    );
    
    public PhoneNumber {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode estar vazio");
        }
        
        String normalized = value.replaceAll("[\\s\\-\\(\\)]", "");
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Formato de telefone inválido");
        }
        
        value = normalized;
    }
    
    public boolean isValid() {
        return value != null && !value.isEmpty();
    }
    
    public String getFormatted() {
        // Implementar formatação brasileira
        if (value.length() == 11) {
            return String.format("(%s) %s-%s", 
                value.substring(0, 2),
                value.substring(2, 7),
                value.substring(7));
        }
        return value;
    }
}

// src/domain/model/Address.java
public record Address(String street, String city, String state, String zipCode) {
    public Address {
        requireNonNull(street, "Rua é obrigatória");
        requireNonNull(city, "Cidade é obrigatória");
        requireNonNull(state, "Estado é obrigatório");
        requireNonNull(zipCode, "CEP é obrigatório");
    }
    
    public boolean isValid() {
        return !street.trim().isEmpty() && 
               !city.trim().isEmpty() && 
               !state.trim().isEmpty() && 
               zipCode.matches("\\d{8}|\\d{5}-\\d{3}");
    }
    
    public boolean isComplete() {
        return isValid() && 
               street.length() > 5 && 
               city.length() > 2;
    }
}
```

4. **Atualizar todas as referências**
```java
// Buscar e substituir em todo o projeto:
// ContactInfo → ContactDetails
```

**Validação:**
```bash
mvn test -Dtest=AntiPatternsFitnessTest#avoidGenericNames
```

---

### 📈 **FASE 3: POLIMENTO (FUTURO)**

#### **7. Implementar Resilience Patterns**

**Status:** ❌ **PENDENTE**  
**Prioridade:** 📈 **MÉDIA**  
**Estimativa:** 12 horas

**Para próximas sprints:**

1. **Circuit Breakers** para chamadas externas
2. **Retry patterns** com backoff exponencial
3. **Health Checks** detalhados
4. **Timeouts** configuráveis
5. **Bulkhead isolation** para thread pools

#### **8. Melhorar Exception Handling**

**Status:** ❌ **PENDENTE**  
**Prioridade:** 📈 **MÉDIA**  
**Estimativa:** 6 horas

1. **ErrorCode enum** padronizado
2. **Exception hierarchy** específica do domínio
3. **Context information** nas exceptions
4. **Logging structured** para observabilidade

---

## 🧪 **VALIDAÇÃO CONTÍNUA**

### **Executar Fitness Functions**

```bash
# Todos os testes arquiteturais
mvn test -Dtest=*FitnessTest

# Específicos por categoria
mvn test -Dtest=AntiPatternsFitnessTest
mvn test -Dtest=DomainDrivenDesignFitnessTest
mvn test -Dtest=MessagingArchitectureFitnessTest
mvn test -Dtest=ResiliencePatternsFitnessTest
```

### **CI/CD Integration**

```yaml
# .github/workflows/architecture-governance.yml
name: Architecture Governance
on: [push, pull_request]

jobs:
  fitness-functions:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run Fitness Functions
        run: mvn test -Dtest=*FitnessTest
      - name: Fail on Architecture Violations
        if: failure()
        run: |
          echo "❌ Architecture violations detected!"
          echo "Check the violations report for details."
          exit 1
```

---

## 📊 **PROGRESSO TRACKING**

### **Template de Checklist**

```markdown
## Sprint N - Architecture Fixes

### 🔥 Critical Fixes
- [ ] **Domain Purification**
  - [ ] Remove Spring Security from User.java
  - [ ] Create UserDetailsAdapter
  - [ ] Update authentication flow
  - [ ] ✅ Test: `AntiPatternsFitnessTest#domainShouldNotDependOnManyExternalPackages`

- [ ] **Rich Domain Model**
  - [ ] Make fields final
  - [ ] Add business behaviors
  - [ ] Implement domain validations
  - [ ] ✅ Test: `AntiPatternsFitnessTest#domainModelsShouldHaveBehaviors`

- [ ] **Break Circular Dependencies**
  - [ ] Move TokenService to security package
  - [ ] Update imports and configurations
  - [ ] ✅ Test: `AntiPatternsFitnessTest#detectCircularDependenciesInInfrastructure`

### ⚠️ High Priority
- [ ] **Value Objects Implementation**
  - [ ] Create UserName, Email, Password, UserId
  - [ ] Update User.java to use Value Objects
  - [ ] ✅ Test: `AntiPatternsFitnessTest#domainShouldUseValueObjectsInsteadOfPrimitives`

### 📈 Medium Priority
- [ ] **Application Layer Purification**
  - [ ] Create PasswordEncoderPort
  - [ ] Implement adapter
  - [ ] Update UseCases
  - [ ] ✅ Test: `MessagingArchitectureFitnessTest#applicationMustUsePortsForEventPublishing`
```

---

## 🎯 **CRITÉRIOS DE ACEITAÇÃO**

### **Definição de "Done" para cada correção:**

1. ✅ **Fitness Function específico PASSA**
2. ✅ **Testes unitários existentes continuam passando**
3. ✅ **Code review aprovado**
4. ✅ **Documentação atualizada**
5. ✅ **Build completo sem warnings**

### **Critério de Sucesso Geral:**

- **Taxa de conformidade > 95%**
- **Zero violações críticas**
- **Todas as camadas respeitam responsabilidades**
- **Domain 100% puro**

---

**Este guia garante implementação sistêmica e verificável de todas as correções arquiteturais detectadas pelos Fitness Functions.**
