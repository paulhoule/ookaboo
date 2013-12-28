
function elasticSearchLoader {
   java -jar $env:userprofile\.m2\repository\com\\ontology2\elasticSearchLoader\${project.version}\elasticSearchLoader-${project.version}-onejar.jar @args
}