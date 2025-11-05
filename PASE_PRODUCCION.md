## Proceso para pase a producción

Los archivos que se cambian cuando se haga el pase a produccipón son:

``` 
* Descomentar la conexion que diga #PRO y comentar la conexion QAS y local en el archivo application.properties
* En el archivo configuraciones-fox.properties en la propiedad ruta.dbfox = dejar apuntando a la ip y carpeta de producción
* En el archivo configuraciones-viaticos.properties en la propiedad ruta.portal cambiar la ruta al portal producción, en la propiedad rutaArchivos cambiar a la ruta de archivos de producción
```