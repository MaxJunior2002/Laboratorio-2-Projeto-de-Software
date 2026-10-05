package com.projetosft.labdois.modules.simulador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SimuladorMatriculaMain {

    private static final int MAX_OBRIGATORIAS = 4;
    private static final int MAX_OPTATIVAS = 2;

    private final Scanner entrada;
    private final List<Aluno> alunos = new ArrayList<>();
    private final List<Professor> professores = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<Periodo> periodos = new ArrayList<>();
    private final List<Oferta> ofertas = new ArrayList<>();
    private final List<Matricula> matriculas = new ArrayList<>();
    private Usuario usuarioLogado;
    private int proximoAlunoId = 1;
    private int proximoProfessorId = 1;
    private int proximoCursoId = 1;
    private int proximaDisciplinaId = 1;
    private int proximoPeriodoId = 1;
    private int proximaOfertaId = 1;
    private int proximaMatriculaId = 1;

    private SimuladorMatriculaMain(Scanner entrada) {
        this.entrada = entrada;
        carregarDadosIniciais();
    }

    public static void main(String[] args) {
        new SimuladorMatriculaMain(new Scanner(System.in)).executar();
    }

    private void executar() {
        System.out.println("=== Simulador local do sistema de matriculas ===");
        System.out.println("Dados ficticios em memoria. Escolha um perfil para comecar.");

        while (true) {
            mostrarMenuInicial();
            if (!entrada.hasNextLine()) {
                System.out.println("\nEntrada encerrada. Simulador finalizado.");
                return;
            }
            String opcao = entrada.nextLine().trim();
            switch (opcao) {
                case "1" -> executarOperacao(this::iniciarSessaoAluno);
                case "2" -> executarOperacao(this::iniciarSessaoProfessor);
                case "3" -> executarAreaAdministrativa();
                case "0" -> {
                    System.out.println("Simulador encerrado.");
                    return;
                }
                default -> System.out.println("Opcao invalida. Escolha 1, 2, 3 ou 0.");
            }
        }
    }

    private void mostrarMenuInicial() {
        System.out.println();
        System.out.println("=== Menu principal ===");
        System.out.println("1 - Entrar como aluno");
        System.out.println("2 - Entrar como professor");
        System.out.println("3 - Area administrativa (cadastros e periodos)");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }

    private void iniciarSessaoAluno() {
        if (!autenticarAluno()) {
            return;
        }
        if (usuarioLogado instanceof Aluno aluno) {
            executarMenuAluno(aluno);
        }
        usuarioLogado = null;
    }

    private void iniciarSessaoProfessor() {
        if (!autenticarProfessor()) {
            return;
        }
        if (usuarioLogado instanceof Professor professor) {
            executarMenuProfessor(professor);
        }
        usuarioLogado = null;
    }

    private void executarMenuAluno(Aluno aluno) {
        while (usuarioLogado == aluno) {
            System.out.println();
            System.out.println("=== Area do aluno: " + aluno.nome() + " ===");
            System.out.println("1 - Fazer matricula");
            System.out.println("2 - Consultar minhas matriculas e cobrancas");
            System.out.println("3 - Cancelar minha matricula");
            System.out.println("0 - Sair da conta");
            System.out.print("Opcao: ");
            if (!entrada.hasNextLine()) {
                usuarioLogado = null;
                return;
            }
            String opcao = entrada.nextLine().trim();
            executarOperacao(() -> {
                switch (opcao) {
                    case "1" -> criarMatricula();
                    case "2" -> listarMatriculasDoAluno(aluno);
                    case "3" -> cancelarMatricula();
                    case "0" -> usuarioLogado = null;
                    default -> System.out.println("Opcao invalida. Escolha 1, 2, 3 ou 0.");
                }
            });
        }
    }

    private void executarMenuProfessor(Professor professor) {
        while (usuarioLogado == professor) {
            System.out.println();
            System.out.println("=== Area do professor: " + professor.nome() + " ===");
            System.out.println("1 - Consultar minhas turmas");
            System.out.println("0 - Sair da conta");
            System.out.print("Opcao: ");
            if (!entrada.hasNextLine()) {
                usuarioLogado = null;
                return;
            }
            String opcao = entrada.nextLine().trim();
            executarOperacao(() -> {
                switch (opcao) {
                    case "1" -> consultarTurmas();
                    case "0" -> usuarioLogado = null;
                    default -> System.out.println("Opcao invalida. Escolha 1 ou 0.");
                }
            });
        }
    }

    private void executarAreaAdministrativa() {
        System.out.println();
        System.out.println("=== Area administrativa da simulacao ===");
        System.out.println("Nao ha login da Secretaria nesta simulacao.");
        while (true) {
            System.out.println();
            System.out.println("1 - Cadastrar aluno");
            System.out.println("2 - Cadastrar professor");
            System.out.println("3 - Cadastrar curso");
            System.out.println("4 - Cadastrar disciplina");
            System.out.println("5 - Cadastrar periodo de inscricao");
            System.out.println("6 - Criar oferta de disciplina");
            System.out.println("7 - Consultar cadastros e matriculas");
            System.out.println("8 - Encerrar periodo");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Opcao: ");
            if (!entrada.hasNextLine()) {
                return;
            }
            String opcao = entrada.nextLine().trim();
            if (opcao.equals("0")) {
                return;
            }
            executarOperacao(() -> {
                switch (opcao) {
                    case "1" -> cadastrarAluno();
                    case "2" -> cadastrarProfessor();
                    case "3" -> cadastrarCurso();
                    case "4" -> cadastrarDisciplina();
                    case "5" -> cadastrarPeriodo();
                    case "6" -> cadastrarOferta();
                    case "7" -> {
                        listarCadastros();
                        listarMatriculas();
                    }
                    case "8" -> encerrarPeriodo();
                    default -> System.out.println("Opcao invalida. Escolha uma opcao de 1 a 8 ou 0.");
                }
            });
        }
    }

    private void executarOperacao(Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Operacao nao realizada: " + exception.getMessage());
        }
    }

    private void carregarDadosIniciais() {
        Aluno ana = new Aluno(proximoAlunoId++, "Ana Souza", "ana@example.com",
                "senha123", "2026001");
        Aluno bruno = new Aluno(proximoAlunoId++, "Bruno Lima", "bruno@example.com",
                "senha123", "2026002");
        Aluno caio = new Aluno(proximoAlunoId++, "Caio Silva", "caio@example.com",
                "senha123", "2026003");
        Aluno diana = new Aluno(proximoAlunoId++, "Diana Costa", "diana@example.com",
                "senha123", "2026004");
        alunos.addAll(List.of(ana, bruno, caio, diana));

        Professor carlos = new Professor(proximoProfessorId++, "Carlos Lima",
                "carlos@example.com", "senha123", "P-1001");
        Professor marina = new Professor(proximoProfessorId++, "Marina Alves",
                "marina@example.com", "senha123", "P-1002");
        professores.addAll(List.of(carlos, marina));

        Curso curso = new Curso(proximoCursoId++, "Engenharia de Software", 40);
        cursos.add(curso);
        Disciplina programacao = new Disciplina(
                proximaDisciplinaId++, "Programacao", 60, curso, carlos);
        Disciplina bancoDados = new Disciplina(
                proximaDisciplinaId++, "Banco de Dados", 60, curso, carlos);
        Disciplina engenharia = new Disciplina(
                proximaDisciplinaId++, "Engenharia de Software", 60, curso, marina);
        Disciplina interacao = new Disciplina(
                proximaDisciplinaId++, "Interacao Humano-Computador", 60, curso, marina);
        disciplinas.addAll(List.of(programacao, bancoDados, engenharia, interacao));

        Periodo periodo = new Periodo(proximoPeriodoId++, "2026.2",
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(7));
        periodos.add(periodo);
        ofertas.add(new Oferta(proximaOfertaId++, programacao, periodo, carlos, 3, 3));
        ofertas.add(new Oferta(proximaOfertaId++, bancoDados, periodo, carlos, 3, 3));
        ofertas.add(new Oferta(proximaOfertaId++, engenharia, periodo, marina, 3, 3));
        ofertas.add(new Oferta(proximaOfertaId++, interacao, periodo, marina, 3, 3));
    }

    private void cadastrarAluno() {
        String nome = lerObrigatorio("Nome: ");
        String email = validarEmailDisponivel(lerObrigatorio("Email: "));
        String senha = lerObrigatorio("Senha: ");
        String numero = lerObrigatorio("Numero de matricula: ");
        if (alunos.stream().anyMatch(aluno -> aluno.numeroMatricula().equalsIgnoreCase(numero))) {
            throw new IllegalArgumentException("Numero de matricula ja cadastrado.");
        }
        alunos.add(new Aluno(proximoAlunoId++, nome, email, senha, numero));
        System.out.println("Aluno cadastrado.");
    }

    private void cadastrarProfessor() {
        String nome = lerObrigatorio("Nome: ");
        String email = validarEmailDisponivel(lerObrigatorio("Email: "));
        String senha = lerObrigatorio("Senha: ");
        String identificador = lerObrigatorio("Identificador funcional: ");
        if (professores.stream()
                .anyMatch(professor -> professor.identificador().equalsIgnoreCase(identificador))) {
            throw new IllegalArgumentException("Identificador funcional ja cadastrado.");
        }
        professores.add(new Professor(proximoProfessorId++, nome, email, senha, identificador));
        System.out.println("Professor cadastrado.");
    }

    private void cadastrarCurso() {
        String nome = lerObrigatorio("Nome do curso: ");
        int creditos = lerInteiro("Numero de creditos: ");
        if (creditos < 0) {
            throw new IllegalArgumentException("O numero de creditos nao pode ser negativo.");
        }
        if (cursos.stream().anyMatch(curso -> curso.nome().equalsIgnoreCase(nome))) {
            throw new IllegalArgumentException("Curso ja cadastrado.");
        }
        cursos.add(new Curso(proximoCursoId++, nome, creditos));
        System.out.println("Curso cadastrado.");
    }

    private void cadastrarDisciplina() {
        listarCursos();
        Curso curso = buscarCurso(lerInteiro("Numero do curso: "));
        listarProfessores();
        Professor professor = buscarProfessor(lerInteiro("Numero do professor responsavel: "));
        String nome = lerObrigatorio("Nome da disciplina: ");
        int cargaHoraria = lerInteiro("Carga horaria: ");
        if (cargaHoraria < 1) {
            throw new IllegalArgumentException("A carga horaria deve ser positiva.");
        }
        disciplinas.add(new Disciplina(
                proximaDisciplinaId++, nome, cargaHoraria, curso, professor));
        System.out.println("Disciplina cadastrada.");
    }

    private void cadastrarPeriodo() {
        String nome = lerObrigatorio("Identificador do periodo letivo (ex.: 2027.1): ");
        if (buscarPeriodoPorNome(nome) != null) {
            throw new IllegalArgumentException("Periodo ja cadastrado.");
        }
        LocalDate inicio = lerData("Inicio da inscricao (AAAA-MM-DD): ");
        LocalDate fim = lerData("Fim da inscricao (AAAA-MM-DD): ");
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final nao pode anteceder a inicial.");
        }
        periodos.add(new Periodo(proximoPeriodoId++, nome, inicio, fim));
        System.out.println("Periodo cadastrado. Os limites das datas de inscricao sao inclusivos.");
    }

    private void cadastrarOferta() {
        listarDisciplinas();
        Disciplina disciplina = buscarDisciplina(lerInteiro("Numero da disciplina: "));
        listarPeriodos();
        Periodo periodo = buscarPeriodo(lerInteiro("Numero do periodo: "));
        if (periodo.encerrado()) {
            throw new IllegalStateException("Nao e possivel criar oferta para periodo encerrado.");
        }
        if (ofertas.stream().anyMatch(oferta ->
                oferta.disciplina().id() == disciplina.id()
                        && oferta.periodo().id() == periodo.id())) {
            throw new IllegalArgumentException("A disciplina ja tem oferta nesse periodo.");
        }
        listarProfessores();
        Professor professor = buscarProfessor(lerInteiro("Numero do professor da oferta: "));
        int capacidade = lerInteiro("Capacidade maxima (padrao sugerido 60): ");
        int minimo = lerInteiro("Minimo de alunos (padrao sugerido 3): ");
        if (capacidade < 1 || minimo < 1 || minimo > capacidade) {
            throw new IllegalArgumentException("Capacidade e minimo devem ser positivos; minimo <= capacidade.");
        }
        ofertas.add(new Oferta(
                proximaOfertaId++, disciplina, periodo, professor, capacidade, minimo));
        System.out.println("Oferta criada.");
    }

    private boolean autenticarAluno() {
        System.out.println("\n=== Login do aluno ===");
        System.out.println("Exemplo: ana@example.com / senha123");
        String email = lerObrigatorio("Email: ");
        String senha = lerObrigatorio("Senha: ");
        for (Aluno aluno : alunos) {
            if (aluno.email().equalsIgnoreCase(email) && aluno.senha().equals(senha)) {
                usuarioLogado = aluno;
                System.out.println("Bem-vindo(a), " + aluno.nome() + ".");
                return true;
            }
        }
        System.out.println("Email ou senha invalidos. Confira os dados e tente novamente.");
        return false;
    }

    private boolean autenticarProfessor() {
        System.out.println("\n=== Login do professor ===");
        System.out.println("Exemplo: carlos@example.com / senha123");
        String email = lerObrigatorio("Email: ");
        String senha = lerObrigatorio("Senha: ");
        for (Professor professor : professores) {
            if (professor.email().equalsIgnoreCase(email) && professor.senha().equals(senha)) {
                usuarioLogado = professor;
                System.out.println("Bem-vindo(a), professor(a) " + professor.nome() + ".");
                return true;
            }
        }
        System.out.println("Email ou senha invalidos. Confira os dados e tente novamente.");
        return false;
    }

    private void listarCadastros() {
        listarAlunos();
        listarProfessores();
        listarCursos();
        listarDisciplinas();
        listarPeriodos();
        listarOfertas();
    }

    private void listarAlunos() {
        System.out.println("\nAlunos:");
        alunos.forEach(aluno -> System.out.printf(
                "%d - %s | email: %s | matricula: %s%n",
                aluno.id(), aluno.nome(), aluno.email(), aluno.numeroMatricula()));
    }

    private void listarProfessores() {
        System.out.println("\nProfessores:");
        professores.forEach(professor -> System.out.printf(
                "%d - %s | email: %s | identificador: %s%n",
                professor.id(), professor.nome(), professor.email(), professor.identificador()));
    }

    private void listarCursos() {
        System.out.println("\nCursos:");
        cursos.forEach(curso -> System.out.printf(
                "%d - %s | creditos: %d%n", curso.id(), curso.nome(), curso.creditos()));
    }

    private void listarDisciplinas() {
        System.out.println("\nDisciplinas:");
        disciplinas.forEach(disciplina -> System.out.printf(
                "%d - %s | curso: %s | responsavel: %s | carga: %d horas%n",
                disciplina.id(), disciplina.nome(), disciplina.curso().nome(),
                disciplina.professor().nome(), disciplina.cargaHoraria()));
    }

    private void listarPeriodos() {
        System.out.println("\nPeriodos de inscricao:");
        periodos.forEach(periodo -> System.out.printf(
                "%d - %s | %s ate %s | %s%n", periodo.id(), periodo.nome(),
                periodo.inicio(), periodo.fim(), periodo.encerrado() ? "ENCERRADO" : "ABERTO"));
    }

    private void listarOfertas() {
        System.out.println("\nOfertas:");
        if (ofertas.isEmpty()) {
            System.out.println("Nenhuma oferta cadastrada.");
            return;
        }
        ofertas.forEach(oferta -> System.out.printf(
                "%d - %s | periodo: %s | professor: %s | vagas: %d/%d"
                        + " | minimo: %d | estado: %s%n",
                oferta.id(), oferta.disciplina().nome(), oferta.periodo().nome(),
                oferta.professor().nome(), contarMatriculados(oferta),
                oferta.capacidadeMaxima(), oferta.minimoAlunos(), oferta.status()));
    }

    private void criarMatricula() {
        Aluno aluno = exigirAlunoLogado();
        listarOfertas();
        Periodo periodo = buscarPeriodoPorNome(lerObrigatorio("Periodo da matricula: "));
        if (periodo == null) {
            throw new IllegalArgumentException("Periodo nao encontrado.");
        }
        validarJanelaAberta(periodo);
        if (matriculas.stream().anyMatch(matricula ->
                matricula.aluno().id() == aluno.id() && matricula.periodo().id() == periodo.id())) {
            throw new IllegalStateException("O aluno ja possui matricula nesse periodo.");
        }

        List<Oferta> obrigatorias = lerOfertas(periodo, "Ofertas obrigatorias (ate 4, separadas por virgula): ");
        List<Oferta> optativas = lerOfertas(periodo, "Ofertas optativas (ate 2, separadas por virgula): ");
        validarSelecao(obrigatorias, optativas);

        List<Oferta> selecionadas = new ArrayList<>(obrigatorias);
        selecionadas.addAll(optativas);
        for (Oferta oferta : selecionadas) {
            validarOfertaDisponivel(oferta);
        }

        Matricula matricula = new Matricula(
                proximaMatriculaId++, aluno, periodo, obrigatorias, optativas);
        matriculas.add(matricula);
        for (Oferta oferta : selecionadas) {
            if (contarMatriculados(oferta) >= oferta.capacidadeMaxima()) {
                oferta.setStatus("ENCERRADA");
            }
        }
        System.out.println("Matricula " + matricula.id()
                + " criada. Cobranca simulada: SOLICITADA.");
    }

    private List<Oferta> lerOfertas(Periodo periodo, String mensagem) {
        System.out.print(mensagem);
        String linha = entrada.nextLine().trim();
        if (linha.isEmpty()) {
            return List.of();
        }
        List<Oferta> selecionadas = new ArrayList<>();
        for (String valor : linha.split(",")) {
            Oferta oferta = buscarOferta(parseInteiro(valor.trim()));
            if (oferta.periodo().id() != periodo.id()) {
                throw new IllegalArgumentException("A oferta nao pertence ao periodo selecionado.");
            }
            selecionadas.add(oferta);
        }
        return selecionadas;
    }

    private void validarSelecao(List<Oferta> obrigatorias, List<Oferta> optativas) {
        if (obrigatorias.isEmpty() && optativas.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma oferta.");
        }
        if (obrigatorias.size() > MAX_OBRIGATORIAS) {
            throw new IllegalArgumentException("O limite e de 4 ofertas obrigatorias.");
        }
        if (optativas.size() > MAX_OPTATIVAS) {
            throw new IllegalArgumentException("O limite e de 2 ofertas optativas.");
        }
        Set<Integer> ids = new HashSet<>();
        if (obrigatorias.stream().anyMatch(oferta -> !ids.add(oferta.id()))
                || optativas.stream().anyMatch(oferta -> !ids.add(oferta.id()))) {
            throw new IllegalArgumentException("Uma oferta nao pode ser selecionada mais de uma vez.");
        }
    }

    private void validarOfertaDisponivel(Oferta oferta) {
        if (!oferta.status().equals("ABERTA") && !oferta.status().equals("ATIVA")) {
            throw new IllegalStateException("A oferta " + oferta.disciplina().nome()
                    + " nao esta recebendo matriculas.");
        }
        if (contarMatriculados(oferta) >= oferta.capacidadeMaxima()) {
            oferta.setStatus("ENCERRADA");
            throw new IllegalStateException("A oferta atingiu a capacidade maxima.");
        }
    }

    private void listarMatriculas() {
        if (matriculas.isEmpty()) {
            System.out.println("Ainda nao ha matriculas.");
            return;
        }
        matriculas.forEach(matricula -> System.out.printf(
                "%d - %s | aluno: %s | periodo: %s | obrigatorias: %d | optativas: %d"
                        + " | estado: %s | cobranca: %s%n",
                matricula.id(), matricula.data(), matricula.aluno().nome(), matricula.periodo().nome(),
                matricula.obrigatorias().size(), matricula.optativas().size(),
                matricula.ativa() ? "ATIVA" : "CANCELADA", matricula.cobranca()));
    }

    private void listarMatriculasDoAluno(Aluno aluno) {
        List<Matricula> matriculasDoAluno = matriculas.stream()
                .filter(matricula -> matricula.aluno().id() == aluno.id())
                .toList();
        if (matriculasDoAluno.isEmpty()) {
            System.out.println("Voce ainda nao possui matriculas.");
            return;
        }
        matriculasDoAluno.forEach(matricula -> System.out.printf(
                "%d - periodo: %s | obrigatorias: %d | optativas: %d"
                        + " | estado: %s | cobranca: %s%n",
                matricula.id(), matricula.periodo().nome(),
                matricula.obrigatorias().size(), matricula.optativas().size(),
                matricula.ativa() ? "ATIVA" : "CANCELADA", matricula.cobranca()));
    }

    private void cancelarMatricula() {
        Aluno aluno = exigirAlunoLogado();
        listarMatriculas();
        Matricula matricula = buscarMatricula(lerInteiro("Numero da matricula: "));
        if (matricula.aluno().id() != aluno.id()) {
            throw new IllegalStateException("O aluno autenticado so pode cancelar sua propria matricula.");
        }
        if (!matricula.ativa()) {
            throw new IllegalStateException("A matricula ja foi cancelada.");
        }
        validarJanelaAberta(matricula.periodo());
        List<Oferta> selecionadas = new ArrayList<>(matricula.obrigatorias());
        selecionadas.addAll(matricula.optativas());
        matricula.cancelar();
        for (Oferta oferta : selecionadas) {
            if (oferta.status().equals("ENCERRADA")
                    && contarMatriculados(oferta) < oferta.capacidadeMaxima()) {
                oferta.setStatus("ABERTA");
            }
        }
        System.out.println("Matricula cancelada. Cobranca simulada: CANCELADA.");
    }

    private void consultarTurmas() {
        Professor professor = exigirProfessorLogado();
        String periodo = lerObrigatorio("Identificador do periodo: ");
        boolean encontrouOferta = false;
        for (Oferta oferta : ofertas) {
            if (oferta.professor().id() != professor.id()
                    || !oferta.periodo().nome().equalsIgnoreCase(periodo)) {
                continue;
            }
            encontrouOferta = true;
            System.out.println("\n" + oferta.disciplina().nome() + " | estado: " + oferta.status());
            List<Matricula> matriculasOferta = matriculas.stream()
                    .filter(Matricula::ativa)
                    .filter(matricula -> matricula.obrigatorias().contains(oferta)
                            || matricula.optativas().contains(oferta))
                    .toList();
            if (matriculasOferta.isEmpty()) {
                System.out.println("Sem alunos ativos.");
            }
            matriculasOferta.forEach(matricula -> System.out.println(
                    "- " + matricula.aluno().nome() + " (" + matricula.aluno().numeroMatricula() + ")"));
        }
        if (!encontrouOferta) {
            System.out.println("Nenhuma oferta encontrada para esse professor e periodo.");
        }
    }

    private void encerrarPeriodo() {
        listarPeriodos();
        Periodo periodo = buscarPeriodo(lerInteiro("Numero do periodo a encerrar: "));
        if (periodo.encerrado()) {
            throw new IllegalStateException("O periodo ja foi encerrado.");
        }
        int ativadas = 0;
        int canceladas = 0;
        for (Oferta oferta : ofertas) {
            if (oferta.periodo().id() != periodo.id()) {
                continue;
            }
            if (contarMatriculados(oferta) >= oferta.minimoAlunos()) {
                oferta.setStatus("ATIVA");
                ativadas++;
            } else {
                oferta.setStatus("CANCELADA");
                canceladas++;
            }
        }
        periodo.encerrar();
        System.out.printf("Periodo %s encerrado para demonstracao: %d oferta(s) ATIVA(S),"
                        + " %d oferta(s) CANCELADA(S).%n",
                periodo.nome(), ativadas, canceladas);
    }

    private int contarMatriculados(Oferta oferta) {
        return (int) matriculas.stream()
                .filter(Matricula::ativa)
                .filter(matricula -> matricula.obrigatorias().contains(oferta)
                        || matricula.optativas().contains(oferta))
                .count();
    }

    private void validarJanelaAberta(Periodo periodo) {
        LocalDate hoje = LocalDate.now();
        if (periodo.encerrado() || hoje.isBefore(periodo.inicio()) || hoje.isAfter(periodo.fim())) {
            throw new IllegalStateException("O periodo de inscricao esta fechado.");
        }
    }

    private String validarEmailDisponivel(String email) {
        String normalizado = email.trim();
        boolean existe = alunos.stream().anyMatch(aluno -> aluno.email().equalsIgnoreCase(normalizado))
                || professores.stream().anyMatch(professor -> professor.email().equalsIgnoreCase(normalizado));
        if (existe || !normalizado.contains("@")) {
            throw new IllegalArgumentException(existe
                    ? "Email ja cadastrado."
                    : "Informe um email valido.");
        }
        return normalizado;
    }

    private Aluno exigirAlunoLogado() {
        if (usuarioLogado instanceof Aluno aluno) {
            return aluno;
        }
        throw new IllegalStateException("Faca login com uma conta de aluno.");
    }

    private Professor exigirProfessorLogado() {
        if (usuarioLogado instanceof Professor professor) {
            return professor;
        }
        throw new IllegalStateException("Faca login com uma conta de professor.");
    }

    private Professor buscarProfessor(int id) {
        return professores.stream().filter(professor -> professor.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Professor inexistente."));
    }

    private Curso buscarCurso(int id) {
        return cursos.stream().filter(curso -> curso.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Curso inexistente."));
    }

    private Disciplina buscarDisciplina(int id) {
        return disciplinas.stream().filter(disciplina -> disciplina.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Disciplina inexistente."));
    }

    private Periodo buscarPeriodo(int id) {
        return periodos.stream().filter(periodo -> periodo.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Periodo inexistente."));
    }

    private Periodo buscarPeriodoPorNome(String nome) {
        return periodos.stream().filter(periodo -> periodo.nome().equalsIgnoreCase(nome.trim()))
                .findFirst().orElse(null);
    }

    private Oferta buscarOferta(int id) {
        return ofertas.stream().filter(oferta -> oferta.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Oferta inexistente."));
    }

    private Matricula buscarMatricula(int id) {
        return matriculas.stream().filter(matricula -> matricula.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Matricula inexistente."));
    }

    private String lerObrigatorio(String mensagem) {
        System.out.print(mensagem);
        String valor = entrada.nextLine().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException("O valor informado e obrigatorio.");
        }
        return valor;
    }

    private LocalDate lerData(String mensagem) {
        String valor = lerObrigatorio(mensagem);
        try {
            return LocalDate.parse(valor);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Use o formato de data AAAA-MM-DD.");
        }
    }

    private int lerInteiro(String mensagem) {
        return parseInteiro(lerObrigatorio(mensagem));
    }

    private int parseInteiro(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Informe um numero inteiro valido.");
        }
    }

    private interface Usuario {
        int id();
        String nome();
        String email();
        String senha();
        String tipo();
    }

    private record Aluno(
            int id, String nome, String email, String senha, String numeroMatricula) implements Usuario {
        @Override
        public String tipo() {
            return "ALUNO";
        }
    }

    private record Professor(
            int id, String nome, String email, String senha, String identificador) implements Usuario {
        @Override
        public String tipo() {
            return "PROFESSOR";
        }

    }

    private record Curso(int id, String nome, int creditos) {
    }

    private record Disciplina(
            int id, String nome, int cargaHoraria, Curso curso, Professor professor) {
    }

    private static final class Periodo {
        private final int id;
        private final String nome;
        private final LocalDate inicio;
        private final LocalDate fim;
        private boolean encerrado;

        private Periodo(int id, String nome, LocalDate inicio, LocalDate fim) {
            this.id = id;
            this.nome = nome;
            this.inicio = inicio;
            this.fim = fim;
        }

        private int id() {
            return id;
        }

        private String nome() {
            return nome;
        }

        private LocalDate inicio() {
            return inicio;
        }

        private LocalDate fim() {
            return fim;
        }

        private boolean encerrado() {
            return encerrado;
        }

        private void encerrar() {
            encerrado = true;
        }
    }

    private static final class Oferta {
        private final int id;
        private final Disciplina disciplina;
        private final Periodo periodo;
        private final Professor professor;
        private final int capacidadeMaxima;
        private final int minimoAlunos;
        private String status = "ABERTA";

        private Oferta(
                int id, Disciplina disciplina, Periodo periodo, Professor professor,
                int capacidadeMaxima, int minimoAlunos) {
            this.id = id;
            this.disciplina = disciplina;
            this.periodo = periodo;
            this.professor = professor;
            this.capacidadeMaxima = capacidadeMaxima;
            this.minimoAlunos = minimoAlunos;
        }

        private int id() {
            return id;
        }

        private Disciplina disciplina() {
            return disciplina;
        }

        private Periodo periodo() {
            return periodo;
        }

        private Professor professor() {
            return professor;
        }

        private int capacidadeMaxima() {
            return capacidadeMaxima;
        }

        private int minimoAlunos() {
            return minimoAlunos;
        }

        private String status() {
            return status;
        }

        private void setStatus(String status) {
            this.status = status;
        }
    }

    private static final class Matricula {
        private final int id;
        private final Aluno aluno;
        private final Periodo periodo;
        private final LocalDate data = LocalDate.now();
        private final List<Oferta> obrigatorias;
        private final List<Oferta> optativas;
        private boolean ativa = true;
        private String cobranca = "SOLICITADA";

        private Matricula(
                int id, Aluno aluno, Periodo periodo,
                List<Oferta> obrigatorias, List<Oferta> optativas) {
            this.id = id;
            this.aluno = aluno;
            this.periodo = periodo;
            this.obrigatorias = List.copyOf(obrigatorias);
            this.optativas = List.copyOf(optativas);
        }

        private int id() {
            return id;
        }

        private Aluno aluno() {
            return aluno;
        }

        private Periodo periodo() {
            return periodo;
        }

        private LocalDate data() {
            return data;
        }

        private List<Oferta> obrigatorias() {
            return obrigatorias;
        }

        private List<Oferta> optativas() {
            return optativas;
        }

        private boolean ativa() {
            return ativa;
        }

        private String cobranca() {
            return cobranca;
        }

        private void cancelar() {
            ativa = false;
            cobranca = "CANCELADA";
        }
    }
}
