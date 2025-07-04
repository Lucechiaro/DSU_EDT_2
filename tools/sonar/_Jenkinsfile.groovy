import java.util.concurrent.TimeUnit

def commonMethods

def BIN_CATALOG = ''
def ACC_PROPERTIES = ''
def ACC_CLASSIFICATION_ERROR = ''
def ACC_BASE = ''
def ACC_USER = ''
def CURRENT_CATALOG = ''
def TEMP_CATALOG = ''
def PROJECT_NAME_EDT = ''
def PROJECT_KEY
def EDT_VALIDATION_RESULT = ''
def GENERIC_ISSUE_JSON = ''
def SRC = ''
def PROJECT_URL = ''
def sonar_host = ''

pipeline {
    parameters {
        string(defaultValue: "${env.PROJECT_NAME}", description: '* Имя проекта. Одинаковое для EDT, проекта в АПК и в сонаре. Обычно совпадает с именем конфигурации.', name: 'PROJECT_NAME')
        string(defaultValue: "${env.git_repo_url}", description: '* URL к гит-репозиторию, который необходимо проверить.', name: 'git_repo_url')
        string(defaultValue: "${env.git_repo_branch}", description: 'Ветка репозитория, которую необходимо проверить. По умолчанию master', name: 'git_repo_branch')
        string(defaultValue: "${env.sonar_catalog}", description: 'Каталог сонара, в котором лежит все, что нужно. По умолчанию C:/Sonar/', name: 'sonar_catalog')
        string(defaultValue: "${env.PROPERTIES_CATALOG}", description: 'Каталог с настройками acc.properties sonar-project.properties. По умолчанию ./Sonar', name: 'PROPERTIES_CATALOG')
        booleanParam(defaultValue: env.ACC_check == null ? true : env.ACC_check, description: 'Выполнять ли проверку АПК. Если нет, то будут получены существующие результаты. По умолчанию: true', name: 'ACC_check')
        booleanParam(defaultValue: env.ACC_recreateProject == null ? false : env.ACC_recreateProject, description: 'Пересоздать проект в АПК. Все данные о проекте будут собраны заново. По умолчанию: false', name: 'ACC_recreateProject')
        string(defaultValue: "${env.STEBI_SETTINGS}", description: 'Файл настроек для переопределения замечаний. Для файла из репо проекта должен начинатся с папки Repo, например .Repo/Sonar/settings.json. По умолчанию ./Sonar/settings.json', name: 'STEBI_SETTINGS')
        string(defaultValue: "${env.jenkinsAgent}", description: 'Нода дженкинса, на которой запускать пайплайн. По умолчанию master', name: 'jenkinsAgent')
        string(defaultValue: "${env.JobWithCover}", description: 'Имя джоба с тестами в котором был выполнен сбор покрытия тестами', name: 'JobWithCover')
        string(defaultValue: "${env.EDT_VERSION}", description: 'Используемая версия EDT. По умолчанию 2020.6.0', name: 'EDT_VERSION')
        string(defaultValue: "${env.rocket_channel}", description: 'Канал в рокет-чате для отправки уведомлений', name: 'rocket_channel')
        booleanParam(defaultValue: env.debug, description: 'Вывод отладочных сообщений.', name: 'debug')
    }
    agent {
    --    label "${(env.jenkinsAgent == null || env.jenkinsAgent == 'null') ? 'master' : env.jenkinsAgent}"
    }
    options {
        timeout(time: 8, unit: TimeUnit.HOURS)
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }
    stages {
        stage("Инициализация") {
            steps {
                script {

                    // Инициализация общего модуля
                    commonMethods = load 'JenkinsFiles/Common.groovy'
                    this.commonMethods = commonMethods

                    if (debug == 'true') {
                        env.LOGOS_CONFIG = 'logger.rootLogger=DEBUG' // Вывод всех отладочных сообщений
                    }

                    rocket_channel = rocket_channel == null || rocket_channel == 'null' ? '' : rocket_channel

                    // Инициализация параметров значениями по умолчанию
                    sonar_catalog = commonMethods.initParam(sonar_catalog, 'C:/Sonar/', 'sonar_catalog')
                    PROPERTIES_CATALOG = commonMethods.initParam(PROPERTIES_CATALOG, './Sonar', 'PROPERTIES_CATALOG')

                    STEBI_SETTINGS = commonMethods.initParam(STEBI_SETTINGS, './Sonar/settings.json', 'STEBI_SETTINGS')
                    git_repo_branch = commonMethods.initParam(git_repo_branch, 'master', 'git_repo_branch')

                    JobWithCover = JobWithCover == null || JobWithCover == 'null' ? '' : JobWithCover
                    EDT_VERSION = commonMethods.initParam(EDT_VERSION, '2020.6.0', 'EDT_VERSION')

                    BIN_CATALOG = "${sonar_catalog}/bin/"
                    ACC_BASE = "${sonar_catalog}/ACC/"
                    ACC_USER = 'Admin'
                    SRC = "./${PROJECT_NAME}/src"

                    // Подготовка переменных по переданным параметрам
                    // Настройки инструментов
                    ACC_PROPERTIES = "./Repo/${PROPERTIES_CATALOG}/acc.properties"
                    if (fileExists(ACC_PROPERTIES)) {
                        commonMethods.log("file exists: ${ACC_PROPERTIES}")
                    } else {
                        commonMethods.log("file does not exist: ${ACC_PROPERTIES}")
                        ACC_PROPERTIES = './Sonar/acc.properties'
                    }

                    // csv файл с параметрами ишузов для выгрузки АПК
                    ACC_CLASSIFICATION_ERROR = "./Repo/${PROPERTIES_CATALOG}/acc.ClassificationError.csv"
                    if (fileExists(ACC_CLASSIFICATION_ERROR)) {
                        commonMethods.log("file exists: ${ACC_CLASSIFICATION_ERROR}")
                        } else {
                        commonMethods.log("file does not exist: ${ACC_CLASSIFICATION_ERROR}")
                        ACC_CLASSIFICATION_ERROR = './Sonar/acc.ClassificationError.csv'
                    }

                    CURRENT_CATALOG = pwd()
                    TEMP_CATALOG = "${CURRENT_CATALOG}\\sonar_temp"
                    EDT_VALIDATION_RESULT = "${TEMP_CATALOG}\\edt-result.csv"
                    CURRENT_CATALOG = "${CURRENT_CATALOG}\\Repo"

                    // создаем/очищаем временный каталог
                    dir(TEMP_CATALOG) {
                        deleteDir()
                        writeFile file: 'acc.json', text: '{"issues": [], "rules": []}'
                        writeFile file: 'edt.json', text: '{"issues": [], "rules": []}'
                    }
                    PROJECT_NAME_EDT = "${CURRENT_CATALOG}\\${PROJECT_NAME}"
                    if (git_repo_branch == 'master') {
                        PROJECT_KEY = PROJECT_NAME
                    } else {
                        PROJECT_KEY = "${PROJECT_NAME}_${git_repo_branch}"
                    }

                    GENERIC_ISSUE_JSON = "${TEMP_CATALOG}/acc.json,${TEMP_CATALOG}/edt.json"
                }
            }
        }
        stage('Checkout') {
            steps {
                script {
                    dir('Repo') {
                        commonMethods.cmd("git lfs install")
                        checkout([$class: 'GitSCM',
                            branches: [[name: "*/${git_repo_branch}"]],
                            browser: [$class: 'GitLab', repoUrl: git_repo_url],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [[$class: 'CheckoutOption', timeout: 60], [$class: 'GitLFSPull'], [$class: 'CleanBeforeCheckout', deleteUntrackedNestedRepositories: true], [$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: false, timeout: 60]],
                            userRemoteConfigs: [[credentialsId: scm.userRemoteConfigs[0].credentialsId, url: "${git_repo_url}"]]])
                    }
                }
            }
        }
        stage('АПК') {
            options {
                timeout(time: 4, unit: TimeUnit.HOURS)
            }
            environment {
                cmd_properties = [
                        "acc.propertiesPaths=${ACC_PROPERTIES}",
                        "acc.catalog=${CURRENT_CATALOG}",
                        "acc.sources=${SRC}",
                        "acc.result=${TEMP_CATALOG}\\acc.json",
                        "acc.projectKey=${PROJECT_KEY}",
                        'acc.exportRules=true',
                        "acc.check=${ACC_check}",
                        "acc.recreateProject=${ACC_recreateProject}",
                        "acc.fileClassificationError=${ACC_CLASSIFICATION_ERROR}"
                    ].join(';')
            }

            steps {
                script {
                    commonMethods.cmd("runner run --ibconnection /F${ACC_BASE} --db-user ${ACC_USER} --command \"${cmd_properties}\" --execute \"${BIN_CATALOG}acc-export.epf\" --ordinaryapp=1")
                }
            }
        }
        stage('EDT') {
            options {
                timeout(time: 30, unit: TimeUnit.MINUTES)
            }
            steps {
                script {
                    if (fileExists("${EDT_VALIDATION_RESULT}")) {
                        commonMethods.cmd("@DEL \"${EDT_VALIDATION_RESULT}\"")
                    }
                    withEnv(['RING_OPTS=-Dfile.encoding=UTF-8 -Dosgi.nl=ru -Duser.language=ru']) {
                        commonMethods.cmd("ring edt@${EDT_VERSION} workspace validate --workspace-location \"${TEMP_CATALOG}\" --file \"${EDT_VALIDATION_RESULT}\" --project-list \"${PROJECT_NAME_EDT}\"")
                    }
                    
                }
            }
        }
        stage('Конвертация результатов EDT') {
            steps {
                script {
                    dir('Repo') {
                        commonMethods.cmd("""
                        set SRC=\"${SRC}\"
                        stebi convert -e \"${EDT_VALIDATION_RESULT}\" \"${TEMP_CATALOG}/edt.json\"
                        """)
                    }
                }
            }
        }
        stage('Трансформация результатов') {
            steps {
                script {
                    commonMethods.cmd("""
                    set GENERIC_ISSUE_SETTINGS_JSON=\"${STEBI_SETTINGS}\"
                    set GENERIC_ISSUE_JSON=${GENERIC_ISSUE_JSON}
                    set SRC=\"./Repo/${SRC}\"

                    stebi transform -r=0
                    """)
                }
            }
        }
        stage('Получение покрытия') {
            when { expression { !JobWithCover.isEmpty() } }
            steps {
                script {
                    copyArtifacts flatten: true, optional: true, filter: 'repo/.coverage/coverage.xml', projectName: JobWithCover, selector: workspace(), target: TEMP_CATALOG
                }
            }
        }
        stage('Сканер') {
            steps {
                script {
                    dir('Repo') {
                        withSonarQubeEnv('Sonar') {

                            def scanner_properties = [
                                "-X",
                                "-Dsonar.projectVersion=%SONAR_PROJECTVERSION%",
                                "-Dsonar.projectKey=${PROJECT_KEY}",
                                "-Dsonar.sources=\"${SRC}\"",
                                "-Dsonar.externalIssuesReportPaths=${GENERIC_ISSUE_JSON}",
                                "-Dsonar.sourceEncoding=UTF-8",
                                "-Dsonar.inclusions=**/*.bsl",
                                "-Dsonar.bsl.languageserver.enabled=true",
                                "-Dfile.encoding=UTF-8"
                            ].join(' ')

                            if (fileExists("${TEMP_CATALOG}/coverage.xml")) {
                                scanner_properties = [
                                    "${scanner_properties}",
                                    "-Dsonar.coverageReportPaths=\"${TEMP_CATALOG}\\coverage.xml\"",
                                    "-Dsonar.bsl.calculateLineCover=true"
                                ].join(' ')
                            }
                            def scannerHome = tool 'SonarQube Scanner';
                            commonMethods.cmd("""
                                @set SRC=\"${SRC}\"
                                @echo %SRC%
                                @call stebi g > temp_SONAR_PROJECTVERSION
                                @set /p SONAR_PROJECTVERSION=<temp_SONAR_PROJECTVERSION
                                @DEL temp_SONAR_PROJECTVERSION
                                @echo %SONAR_PROJECTVERSION%
                                @set SONAR_SCANNER_OPTS=-Xmx6g
                                ${scannerHome}\\bin\\sonar-scanner ${scanner_properties}
                                """)
                            PROJECT_URL = "${env.SONAR_HOST_URL}/dashboard?id=${PROJECT_KEY}"
                            sonar_host = "${env.SONAR_HOST_URL}"
                        }

                        def qg = waitForQualityGate()

                        if (!rocket_channel.isEmpty() ) {
                            rocketSend channel: rocket_channel, message: "Проверка сонаром завершена: [${env.JOB_NAME} ${env.BUILD_NUMBER}](${env.JOB_URL}) СТАТУС: [${qg.status}](${PROJECT_URL}). Затрачено: ${currentBuild.durationString}", rawMessage: true
                        }
                    }
                }
            }
        }
    }
}
