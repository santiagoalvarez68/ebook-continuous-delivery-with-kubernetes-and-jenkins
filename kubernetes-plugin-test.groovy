podTemplate(label: 'labels-must-match',
  containers: [
    containerTemplate(
      name: 'kubectl',
      image: 'jorgeacetozi/kubectl:1.7.0', ttyEnabled: true, command: 'cat'
    ),
    containerTemplate( 
      name: 'mysql', 
      image: 'mysql:5.7', 
      envVars: [
        envVar(key: 'MYSQL_DATABASE', value: 'notepad'),
        envVar(key: 'MYSQL_ROOT_PASSWORD', value: 'root') 
      ]
    ),
    containerTemplate(
      name: 'maven',
      image: 'jorgeacetozi/maven:3.5.0-jdk-8-alpine', 
      ttyEnabled: true,
      command: 'cat'
    ) 
  ],
  volumes: [
    secretVolume(
      mountPath: 'etc/maven/',
      secretName: 'maven-settings-secret'
    )
  ]
) 
{
  node ('labels-must-match') {
    stage('Checkout the Notepad application') {
      git url: 'https://github.com/jorgeacetozi/notepad.git', branch: "${GIT_BRANCH}"
    }
    
    stage('Maven package') { 
      container('maven') {
        sh 'mvn -s /etc/maven/settings.xml clean package' 
      }
    }

    stage('kubectl example') {
      container('kubectl') {
        withCredentials([file(credentialsId: 'k8s-kubeconfigAdmin', variable: 'KUBECONFIG')]) {
          sh 'kubectl get pods'
        }
      }
    }
//    k8s-kubeconfigAdmin
  }
}