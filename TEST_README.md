# Documentação da Suíte de Testes - Sistema Barbearia

## Visão Geral
Esta suíte de testes fornece cobertura abrangente para a aplicação Barbearia, incluindo testes unitários para todos os controllers e testes de integração para autenticação, autorização e fluxos completos de requisições HTTP.

## Estrutura de Testes

### Testes Unitários
Localizados em `src/test/java/com/barbearia/Barbearia/Controller/`

#### 1. UserControllerTest
Testes para operações relacionadas ao usuário incluindo:
- Redirecionamento raiz para página inicial
- Acesso à página inicial (autenticado e não autenticado)
- Página de login com mensagens de erro/sucesso
- Página de registro e fluxo de registro de usuário
- Validação de senha e tratamento de email duplicado
- Acesso à página de agendamento para diferentes funções de usuário (barbeiro vs cliente)
- Acesso e atualizações de página da instituição

**Cenários de Teste Principais:**
- ✅ Usuário não autenticado pode visualizar página inicial
- ✅ Usuário autenticado vê saudação personalizada
- ✅ Erros de login são exibidos corretamente
- ✅ Sucesso no registro redireciona para login
- ✅ Registro com email duplicado é rejeitado
- ✅ Incompatibilidade de senha é detectada
- ✅ Usuários barbeiros veem visualização específica de agendamento
- ✅ Usuários clientes veem visualização específica de agendamento

#### 2. FeedbackControllerTest
Testes para envio e exibição de feedback:
- Listagem de feedback para todos os usuários
- Envio de feedback apenas para usuários autenticados
- Exibição de modal de sucesso após envio
- Associação correta do usuário com feedback
- Atribuição de data ao feedback

**Cenários de Teste Principais:**
- ✅ Usuários não autenticados podem visualizar feedbacks
- ✅ Usuários autenticados veem interface personalizada
- ✅ Envio de feedback requer autenticação
- ✅ Parâmetro de sucesso aciona exibição de modal
- ✅ Feedback é corretamente associado ao usuário logado
- ✅ Data atual é definida no envio do feedback

#### 3. BarbeiroControllerTest
Testes para gerenciamento de barbeiros:
- Criação de barbeiro com validação
- Detecção de email duplicado
- Validação de correspondência de senha
- Exclusão de barbeiro
- Tratamento de mensagens de erro via sessão

**Cenários de Teste Principais:**
- ✅ Criação de barbeiro válida é bem-sucedida
- ✅ Email duplicado mostra modal de erro
- ✅ Incompatibilidade de senha é rejeitada
- ✅ Campos vazios são validados
- ✅ Exclusão de barbeiro é bem-sucedida
- ✅ Exclusão de barbeiro inexistente mostra erro
- ✅ Mensagens de erro são armazenadas na sessão

#### 4. AgendamentoControllerTest
Testes para agendamento de horários:
- Criação de agendamento com/sem upload de foto
- Agendamentos de doador (doação de cabelo)
- Tratamento de horários indisponíveis
- Cancelamento de agendamento
- Conclusão de agendamento (apenas barbeiro)
- Extração de email do usuário da autenticação

**Cenários de Teste Principais:**
- ✅ Cliente pode criar agendamento com foto
- ✅ Criação de agendamento sem foto funciona
- ✅ Agendamentos de doador são tratados corretamente
- ✅ Horários indisponíveis retornam INDISPONIVEL
- ✅ Erros genéricos retornam resposta ERRO
- ✅ Cancelamento redireciona com parâmetro de sucesso
- ✅ Conclusão redireciona com parâmetro concluído
- ✅ Email do usuário é extraído do contexto de autenticação

#### 5. InstituicaoControllerTest
Testes para gerenciamento de instituições:
- Visualização de instituição (autenticado e não autenticado)
- Listagem de instituições inscritas
- Salvamento de instituição
- Exibição de modal de sucesso

**Cenários de Teste Principais:**
- ✅ Usuário não autenticado pode visualizar instituição
- ✅ Usuário autenticado vê dados personalizados
- ✅ Parâmetro de sucesso aciona modal
- ✅ Listagem de instituições funciona
- ✅ Salvamento de instituição redireciona corretamente

#### 6. ServicoControllerTest
Testes para gerenciamento de serviços:
- Listagem de serviços
- Criação de serviço
- Atualização de serviço
- Exclusão de serviço
- Busca de serviço por ID

**Cenários de Teste Principais:**
- ✅ Listagem de serviços exibe todos os serviços
- ✅ Criação de serviço é bem-sucedida
- ✅ Atualização de serviço funciona corretamente
- ✅ Exclusão de serviço redireciona com sucesso
- ✅ Busca por ID retorna serviço correto

### Testes de Integração
Localizados em `src/test/java/com/barbearia/Barbearia/integration/`

#### BarbeariaIntegrationTest

##### Testes de Autenticação
- Acesso não autenticado a páginas públicas (home, login, registro)
- Requisitos de acesso autenticado para páginas protegidas
- Redirecionamentos de login para usuários não autenticados
- Requisito de autenticação para envio de feedback

##### Testes de Autorização
- Acesso de função de cliente ao agendamento de cliente
- Acesso de função de barbeiro ao agendamento de barbeiro
- Operações exclusivas de barbeiro (criar/excluir barbeiro)
- Renderização de visualização baseada em função

##### Testes de Integração de Fluxo Completo
- Fluxo completo de registro de usuário
- Login com mensagem de sucesso após registro
- Envio de feedback e exibição de modal de sucesso
- Rejeição de registro com email duplicado
- Tratamento de incompatibilidade de senha

##### Testes de Proteção CSRF
- Requisições POST sem token CSRF são rejeitadas (403 Forbidden)
- Requisições POST com token CSRF são bem-sucedidas
- Todas as operações que alteram estado requerem token CSRF

##### Testes de Recursos Estáticos
- Acesso não autenticado a imagens estáticas
- Arquivos CSS acessíveis sem autenticação
- Configuração de segurança para /img/** e /CSS/**

##### Testes de Tratamento de Erros
- Email de barbeiro duplicado mostra modal de erro
- Exclusão de barbeiro inexistente mostra erro
- Mensagens de erro na sessão persistem entre redirecionamentos

##### Testes de Gerenciamento de Sessão
- Mensagens de erro armazenadas na sessão
- Parâmetros de sucesso passados via URL
- Atributos de sessão acessíveis em requisições subsequentes

##### Testes de Acesso Baseado em Função
- Clientes não podem acessar funções exclusivas de barbeiro
- Barbeiros têm permissões elevadas
- Aplicação de funções no nível do controller

##### Testes de Redirecionamento
- Raiz (/) redireciona para /home
- Parâmetro de erro de login mostra mensagem de erro
- Parâmetro de erro de registro mostra erro específico
- Parâmetros de sucesso acionam modais apropriados

## Configuração de Teste

### Propriedades de Teste
Localização: `src/test/resources/application-test.properties`

**Configurações Principais:**
- Banco de dados H2 em memória para testes isolados
- Esquema auto-criado/descartado para cada execução de teste
- Log SQL habilitado para depuração
- Cache Thymeleaf desabilitado para testes
- Log de segurança no nível DEBUG

### Dependências de Teste (do pom.xml)
- `spring-boot-starter-test`: Framework de teste principal
- `spring-security-test`: Utilitários de teste de segurança
- `h2`: Banco de dados em memória para testes
- JUnit 5: Executor de testes
- Mockito: Framework de mock

## Executando os Testes

### Executar Todos os Testes
```bash
mvn test
```

### Executar Classe de Teste Específica
```bash
mvn test -Dtest=UserControllerTest
mvn test -Dtest=FeedbackControllerTest
mvn test -Dtest=BarbeiroControllerTest
mvn test -Dtest=AgendamentoControllerTest
mvn test -Dtest=InstituicaoControllerTest
mvn test -Dtest=ServicoControllerTest
mvn test -Dtest=BarbeariaIntegrationTest
```

### Executar Método de Teste Específico
```bash
mvn test -Dtest=UserControllerTest#testGetIndexAuthenticated
mvn test -Dtest=BarbeariaIntegrationTest#testCompleteUserRegistrationAndLoginFlow
```

### Executar com Cobertura
```bash
mvn clean test jacoco:report
```

## Anotações de Teste Utilizadas

### Anotações de Teste do Spring Boot
- `@WebMvcTest`: Carrega apenas a camada web para testes de controller
- `@SpringBootTest`: Carrega contexto completo da aplicação para testes de integração
- `@AutoConfigureMockMvc`: Configura MockMvc para testes de integração
- `@Transactional`: Reverte alterações no banco de dados após cada teste
- `@ActiveProfiles("test")`: Ativa perfil de teste

### Anotações de Teste do Spring Security
- `@WithMockUser`: Simula usuário autenticado
- `@WithAnonymousUser`: Simula usuário não autenticado
- `@WithMockUser(authorities = "ROLE_BARBER")`: Simula usuário com função específica

### Anotações do Mockito
- `@MockBean`: Cria beans mock no contexto da aplicação
- `@Autowired`: Injeta dependências

### Anotações do JUnit 5
- `@Test`: Marca métodos de teste
- `@BeforeEach`: Executa antes de cada método de teste

## Estratégia de Mock

### Testes Unitários
- Mock de todas as dependências de serviço
- Testa apenas lógica do controller
- Verifica chamadas de métodos de serviço
- Usa `MockMvc` para simulação de requisição HTTP

### Testes de Integração
- Usa beans reais do contexto da aplicação
- Testa fluxo completo de requisição para resposta
- Verifica interações com banco de dados
- Testa integração com Spring Security

## Exemplos de Asserções

### Asserções de Status
```java
.andExpect(status().isOk())
.andExpect(status().is3xxRedirection())
.andExpect(status().isForbidden())
```

### Asserções de View
```java
.andExpect(view().name("HTML/index"))
```

### Asserções de Model
```java
.andExpect(model().attributeExists("feedbacks"))
.andExpect(model().attribute("isAuthenticated", true))
.andExpect(model().attribute("userName", "João"))
```

### Asserções de Redirecionamento
```java
.andExpect(redirectedUrl("/login?registered=true"))
.andExpect(redirectedUrlPattern("**/login"))
```

### Verificação de Serviço
```java
verify(feedbackService, times(1)).salvarFeedback(any(Feedback.class))
verify(userService, never()).saveRegisterUser(any(RegisterUser.class))
```

## Resumo de Cobertura de Testes

### Controllers Testados: 6/6 (100%)
- ✅ UserController
- ✅ FeedbackController
- ✅ BarbeiroController
- ✅ AgendamentoController
- ✅ InstituicaoController
- ✅ ServicoController

### Categorias de Teste
- ✅ Fluxos de autenticação
- ✅ Verificações de autorização
- ✅ Proteção CSRF
- ✅ Gerenciamento de sessão
- ✅ Tratamento de erros
- ✅ Acesso baseado em função
- ✅ Validação de formulários
- ✅ Interações com banco de dados
- ✅ Upload de arquivos
- ✅ Acesso a recursos estáticos

## Melhores Práticas Implementadas

1. **Isolamento**: Cada teste é independente e pode ser executado em qualquer ordem
2. **Clareza**: Nomes de testes descrevem claramente o que está sendo testado
3. **Arrange-Act-Assert**: Testes seguem o padrão AAA
4. **Mocking**: Dependências externas são mockadas nos testes unitários
5. **Limpeza**: `@Transactional` garante rollback do banco de dados após testes
6. **Dados Realistas**: Dados de teste se assemelham a dados reais da aplicação
7. **Teste de Segurança**: Autenticação e autorização são testadas completamente
8. **Cenários de Erro**: Tanto caminhos de sucesso quanto de falha são testados

## Problemas Comuns e Soluções

### Problema: Testes falham devido a beans ausentes
**Solução**: Certifique-se de que todos os serviços necessários estão mockados com `@MockBean` nos testes unitários

### Problema: Erros de token CSRF
**Solução**: Sempre inclua `.with(csrf())` em requisições POST

### Problema: Autenticação não funciona nos testes
**Solução**: Use anotações `@WithMockUser` ou `@WithAnonymousUser`

### Problema: Estado do banco de dados persiste entre testes
**Solução**: Use `@Transactional` na classe de teste ou métodos

### Problema: Testes de upload de arquivo falhando
**Solução**: Use `MockMultipartFile` para simulação de upload de arquivo

## Melhorias Futuras de Teste

1. Adicionar testes de performance para consultas ao banco de dados
2. Adicionar testes para validação de upload de arquivo
3. Adicionar testes para criação concorrente de agendamento
4. Adicionar testes para criptografia de senha
5. Adicionar testes de endpoint API se endpoints REST forem adicionados
6. Adicionar testes de UI com Selenium/TestContainers
7. Adicionar testes de mutação para verificar qualidade dos testes

## Integração Contínua

Para pipelines de CI/CD, adicione à sua configuração de build:

```yaml
# Exemplo GitHub Actions
- name: Executar testes
  run: mvn clean test
  
- name: Gerar relatório de cobertura
  run: mvn jacoco:report
  
- name: Upload de cobertura
  uses: codecov/codecov-action@v3
```

## Conclusão

Esta suíte de testes fornece cobertura abrangente para a aplicação Barbearia, garantindo confiabilidade, segurança e correção de todas as operações dos controllers. A combinação de testes unitários e testes de integração cria uma rede de segurança robusta para desenvolvimento futuro e refatoração.


#### Authentication Tests
- Unauthenticated access to public pages (home, login, register)
- Authenticated access requirements for protected pages
- Login redirects for unauthenticated users
- Feedback submission authentication requirement

#### Authorization Tests
- Client role access to client agendamento
- Barber role access to barber agendamento
- Barber-only operations (create/delete barbeiro)
- Role-based view rendering

#### Full Flow Integration Tests
- Complete user registration flow
- Login with success message after registration
- Feedback submission and success modal display
- Duplicate email registration rejection
- Password mismatch handling

#### CSRF Protection Tests
- POST requests without CSRF token are rejected (403 Forbidden)
- POST requests with CSRF token succeed
- All state-changing operations require CSRF token

#### Static Resources Tests
- Unauthenticated access to static images
- CSS files accessible without authentication
- Security configuration for /img/** and /CSS/**

#### Error Handling Tests
- Duplicate barber email shows error modal
- Non-existent barber deletion shows error
- Session error messages persist across redirects

#### Session Management Tests
- Error messages stored in session
- Success parameters passed via URL
- Session attributes accessible in subsequent requests

#### Role-Based Access Tests
- Clients cannot access barber-only functions
- Barbeiros have elevated permissions
- Role enforcement at controller level

#### Redirect Tests
- Root (/) redirects to /home
- Login error parameter shows error message
- Registration error parameter shows specific error
- Success parameters trigger appropriate modals

## Test Configuration

### Test Properties
Location: `src/test/resources/application-test.properties`

**Key Configurations:**
- H2 in-memory database for isolated tests
- Auto-create/drop schema for each test run
- SQL logging enabled for debugging
- Thymeleaf cache disabled for testing
- Security logging at DEBUG level

### Test Dependencies (from pom.xml)
- `spring-boot-starter-test`: Core testing framework
- `spring-security-test`: Security testing utilities
- `h2`: In-memory database for tests
- JUnit 5: Test runner
- Mockito: Mocking framework

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserControllerTest
mvn test -Dtest=FeedbackControllerTest
mvn test -Dtest=BarbeiroControllerTest
mvn test -Dtest=AgendamentoControllerTest
mvn test -Dtest=BarbeariaIntegrationTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=UserControllerTest#testGetIndexAuthenticated
mvn test -Dtest=BarbeariaIntegrationTest#testCompleteUserRegistrationAndLoginFlow
```

### Run with Coverage
```bash
mvn clean test jacoco:report
```

## Test Annotations Used

### Spring Boot Test Annotations
- `@WebMvcTest`: Loads only web layer for controller tests
- `@SpringBootTest`: Loads full application context for integration tests
- `@AutoConfigureMockMvc`: Configures MockMvc for integration tests
- `@Transactional`: Rollback database changes after each test
- `@ActiveProfiles("test")`: Activate test profile

### Spring Security Test Annotations
- `@WithMockUser`: Simulates authenticated user
- `@WithAnonymousUser`: Simulates unauthenticated user
- `@WithMockUser(authorities = "ROLE_BARBER")`: Simulates user with specific role

### Mockito Annotations
- `@MockBean`: Creates mock beans in application context
- `@Autowired`: Injects dependencies

### JUnit 5 Annotations
- `@Test`: Marks test methods
- `@BeforeEach`: Runs before each test method

## Mocking Strategy

### Unit Tests
- Mock all service dependencies
- Test only controller logic
- Verify service method calls
- Use `MockMvc` for HTTP request simulation

### Integration Tests
- Use real beans from application context
- Test full request-to-response flow
- Verify database interactions
- Test Spring Security integration

## Assertion Examples

### Status Assertions
```java
.andExpect(status().isOk())
.andExpect(status().is3xxRedirection())
.andExpect(status().isForbidden())
```

### View Assertions
```java
.andExpect(view().name("HTML/index"))
```

### Model Assertions
```java
.andExpect(model().attributeExists("feedbacks"))
.andExpect(model().attribute("isAuthenticated", true))
.andExpect(model().attribute("userName", "João"))
```

### Redirect Assertions
```java
.andExpect(redirectedUrl("/login?registered=true"))
.andExpect(redirectedUrlPattern("**/login"))
```

### Service Verification
```java
verify(feedbackService, times(1)).salvarFeedback(any(Feedback.class))
verify(userService, never()).saveRegisterUser(any(RegisterUser.class))
```

## Test Coverage Summary

### Controllers Tested: 4/4 (100%)
- ✅ UserController
- ✅ FeedbackController
- ✅ BarbeiroController
- ✅ AgendamentoController

### Test Categories
- ✅ Authentication flows
- ✅ Authorization checks
- ✅ CSRF protection
- ✅ Session management
- ✅ Error handling
- ✅ Role-based access
- ✅ Form validation
- ✅ Database interactions
- ✅ File uploads
- ✅ Static resource access

## Best Practices Implemented

1. **Isolation**: Each test is independent and can run in any order
2. **Clarity**: Test names clearly describe what is being tested
3. **Arrange-Act-Assert**: Tests follow AAA pattern
4. **Mocking**: External dependencies are mocked in unit tests
5. **Cleanup**: `@Transactional` ensures database rollback after tests
6. **Realistic Data**: Test data resembles real application data
7. **Security Testing**: Authentication and authorization are thoroughly tested
8. **Error Scenarios**: Both success and failure paths are tested

## Common Issues and Solutions

### Issue: Tests fail due to missing beans
**Solution**: Ensure all required services are mocked with `@MockBean` in unit tests

### Issue: CSRF token errors
**Solution**: Always include `.with(csrf())` in POST requests

### Issue: Authentication not working in tests
**Solution**: Use `@WithMockUser` or `@WithAnonymousUser` annotations

### Issue: Database state persists between tests
**Solution**: Use `@Transactional` on test class or methods

### Issue: File upload tests failing
**Solution**: Use `MockMultipartFile` for file upload simulation

## Future Test Enhancements

1. Add tests for ServicoController and InstituicaoController
2. Add performance tests for database queries
3. Add tests for file upload validation
4. Add tests for concurrent agendamento creation
5. Add tests for password encryption
6. Add API endpoint tests if REST endpoints are added
7. Add UI tests with Selenium/TestContainers
8. Add mutation testing to verify test quality

## Continuous Integration

For CI/CD pipelines, add to your build configuration:

```yaml
# GitHub Actions Example
- name: Run tests
  run: mvn clean test
  
- name: Generate coverage report
  run: mvn jacoco:report
  
- name: Upload coverage
  uses: codecov/codecov-action@v3
```

## Conclusion

This test suite provides comprehensive coverage for the Barbearia application, ensuring reliability, security, and correctness of all controller operations. The combination of unit tests and integration tests creates a robust safety net for future development and refactoring.
