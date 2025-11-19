/*
  panel-project-2nd에서는 일부 모듈이 `data` 모듈에 의존하고 있으며,
  이 파이프라인은 주어진 파라미터에 따라 프로젝트의 다양한 모듈을 빌드하고 배포합니다.

  파라미터:
  - DEPLOY_ENV: 배포 환경을 지정 ('test' 또는 'prod')
  - DATA_DEPENDENT_ONLY: `data` 의존 모듈만 배포할지 여부 (false일 경우 모든 모듈 배포)
*/
pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['test', 'prod'])
        booleanParam(name: 'DATA_DEPENDENT_ONLY')
    }

    environment {
        GITHUB_CREDS = credentials('github-creds')
        GITHUB_USERNAME = "${GITHUB_CREDS_USR}"
        GITHUB_TOKEN = "${GITHUB_CREDS_PSW}"
    }

    stages {
        stage('Interpretation-BE Build') {
            tools {
                jdk 'java-21'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle clean :interpretation:build -x test --refresh-dependencies'
                buildAndDeploy('interpretation', 'panel-service-interpretation2')
            }
        }

        stage('SNV-BE Build') {
            tools {
                jdk 'java-17'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :snv:build'
                buildAndDeploy('snv', 'panel-service-snv2')
            }
        }

        stage('SNV-FE Build') {
            tools {
                jdk 'java-21'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :snv-ui:copyWebResources'
                buildAndDeployFE('snv-ui')
            }
        }

        stage('Interpretation-FE Build') {
            tools {
                jdk 'java-21'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :interpretation-ui:copyWebResources'
                buildAndDeployFE('interpretation-ui')
            }
        }

        stage('BI-Service Build') {
            when {
                expression { !params.DATA_DEPENDENT_ONLY }
            }
            tools {
                jdk 'java-17'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :bi-variant-service:clean :bi-variant-service:build'
                buildAndDeploy('bi-variant-service', 'panel-service-bi-variant')
            }
        }

        stage('Gateway Build') {
            when {
                expression { !params.DATA_DEPENDENT_ONLY }
            }
            tools {
                jdk 'java-17'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :gateway:clean :gateway:build'
                buildAndDeploy('gateway', 'panel-service-gateway')
            }
        }

        stage('Service Build') {
            when {
                expression { !params.DATA_DEPENDENT_ONLY }
            }
            tools {
                jdk 'java-17'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :service:build'
                buildAndDeploy('service', 'lims-panel')
            }
        }

        stage('SNV-Marker Build') {
            when {
                expression { !params.DATA_DEPENDENT_ONLY }
            }
            tools {
                jdk 'java-17'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :snv-marker:build'
                buildAndDeploy('snv-marker', 'panel-service-snv-marker')
            }
        }

        stage('Analysis-Handler Build') {
            when {
                expression { !params.DATA_DEPENDENT_ONLY }
            }
            tools {
                jdk 'java-17'
                gradle 'gradle-8.9'
            }
            steps {
                sh 'gradle :bi-analysis-subscriber:clean :bi-analysis-subscriber:build -x test'
                sh 'mv bi-analysis-subscriber/build/libs/*.jar /data/lims/'
                sh 'sudo systemctl restart panel-service-analysis-handler2'
            }
        }
    }
}

def deployToServer(jarPath, remotePath, service = null) {
    def transfers = [
        sshTransfer(
            sourceFiles: jarPath,
            removePrefix: jarPath.substring(0, jarPath.lastIndexOf('/') + 1),
            remoteDirectory: remotePath,
            execCommand: service ? "sudo systemctl restart ${service}" : '',
        )
    ]

    sshPublisher(
        failOnError: true,
        publishers: [
            sshPublisherDesc(
                configName: 'Aries',
                transfers: transfers,
                verbose: true
            ),
            sshPublisherDesc(
                configName: 'Taurus',
                transfers: transfers,
                verbose: true
            )
        ]
    )
}

def buildAndDeploy(module, serviceName) {
    def jarPath = "${module}/build/libs/*.jar"
    def remotePath = 'lims'
    def tempBuildPath = "/data/${remotePath}"

    if (params.DEPLOY_ENV == 'test') {
        sh "mv ${jarPath} ${tempBuildPath}"
        sh "sudo systemctl restart ${serviceName}"
    } else if (params.DEPLOY_ENV == 'prod') {
        deployToServer(jarPath, remotePath, serviceName)
    }
}

def buildAndDeployFE(module) {
    def sourcePath = "${module}/build/static/"
    def jarPath = "${sourcePath}**"
    def remotePath = 'lims/static/panel-service'
    def tempBuildPath = "/data/${remotePath}/"

    if (params.DEPLOY_ENV == 'test') {
        sh "sudo rsync -a --no-perms ${sourcePath} ${tempBuildPath}"
    } else if (params.DEPLOY_ENV == 'prod') {
        deployToServer(jarPath, remotePath)
    }
}
