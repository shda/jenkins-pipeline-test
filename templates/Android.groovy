@Library('unity') _

pipeline {
    environment {
        AUTO_DETECT_UNITY_VERSION = 'true'
//        AUTO_DETECT_UNITY_VERSION = 'false'
//        UNITY_VERSION = '2022.3.1f1'
        BUILD_TARGET = 'Android' //Required
        PROJECT_DIR = '.' //Required
        GIT_URL = 'https://bitbucket.org/prosaas/cryptotanks-unity.git' //Required
        GIT_BRANCH = 'master' //Required
        GIT_CREDENTIALS_ID = 'bitbucket'

        EXTRA_SCRIPT_DEFINES = ''
    }

    agent {
        label 'unity && unity-pro'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: env.GIT_BRANCH, credentialsId: env.GIT_CREDENTIALS_ID, url: env.GIT_URL
            }
        }
        stage('Build') {
            steps {
                script {
                    def options = [
                            unityHubPath          : UNITY_HUB_PATH,
                            autoDetectUnityVersion: env.AUTO_DETECT_UNITY_VERSION.toBoolean(),
                            unityVersion          : env.UNITY_VERSION,
                            projectDir            : env.PROJECT_DIR,
                            buildTarget           : env.BUILD_TARGET,
                            extraScriptingDefines : env.EXTRA_SCRIPT_DEFINES?.split('[, ]'),
                            buildName             : BUILD_TAG,
                            android               : [
                                    //false: apk, true: aab
                                    buildAppBundle: false,
                                    keystoreName  : 'user.keystore', //in project dir
//                                    keystoreName  : credentials('cryptotanks-keystore'),
                                    keystorePass  : credentials('cryptotanks-keystore-pass'),
                                    keyaliasName  : 'release',
                                    keyaliasPass  : credentials('cryptotanks-keyalias-pass')
                            ]
                    ]
                    def report = unityBuilder.build(options)
                    env.OUTPUT_PATH = report.outputPath
                }
            }
        }
        stage('Cleanup') {
            steps {
                script {
                    new File(env.BUILD_ARCHIVE_PATH).delete()
                }
            }
        }
    }
}
