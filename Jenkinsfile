pipeline {
  agent any

  environment {
    DOCKER_IMAGE = 'docker.io/bayesaermbow/java17-render-app'
  }

  options { timestamps(); ansiColor('xterm') }

  stages {

    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Set Version') {
      steps {
        script {
          // On récupère date et SHA via deux commandes séparées (aucun "$(" dans une chaîne Groovy)
          def ts  = sh(script: 'date +%Y%m%d-%H%M%S',       returnStdout: true).trim()
          def sha = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
          env.VERSION = "${ts}-${sha}"
          echo "Version: ${env.VERSION}"
        }
      }
    }

    stage('Build & Test (Maven)') {
      steps {
        sh 'mvn -B -Dmaven.test.failure.ignore=false clean package'
      }
    }

    stage('Docker Build') {
      steps {
        sh "docker build -t ${DOCKER_IMAGE}:${env.VERSION} -t ${DOCKER_IMAGE}:latest ."
      }
    }

    stage('Docker Login & Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push '"${DOCKER_IMAGE}:${VERSION}"'
            docker push '"${DOCKER_IMAGE}:latest"'
            docker logout
          '''
        }
      }
    }

    stage('Deploy on Render') {
      steps {
        withCredentials([string(credentialsId: 'render-deploy-hook', variable: 'RENDER_DEPLOY_HOOK')]) {
          sh 'curl -fsSL -X POST "$RENDER_DEPLOY_HOOK" || (sleep 5 && curl -fsSL -X POST "$RENDER_DEPLOY_HOOK")'
        }
      }
    }
  }
//fdfddsddzz
  post {
    success { echo "✅ OK — Image: ${DOCKER_IMAGE}:${env.VERSION} — Déploiement Render déclenché." }
    failure { echo "❌ Échec du pipeline." }
  }
}
