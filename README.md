# portal-viaticos-spring-boot

## Versiones
Version java = 1.8
Version spring-boot = 2.3.5.RELEASE

## Componentes
* Utiliza @Scheduled para jobs
* Utiliza com.lowagie para pdfs
* Utiliza JPA

## Que se necesita para envio de correos

Es necesario crear la carpeta en C:/assets, dentro crear las carpetas:
* email-templates
    * Se tienen los templates
* font
    * Dentro de esta carpeta esta la carpeta roboto la cual tiene el font para correos
* logo
    * Dentro de esta carpeta se tiene el logo de SLAPI.png esto es para el pdf
* Se tiene el archivo template-styles el cual contiene el estilo para correos 
```
Nota: se tendra toda la estructura en una carpeta en el proyecto, para que se pueda descargar y copiarlo donde sea necesario
```


## Que se necesita para filesystem

Es necesario crear la carpeta filesystem en C:/ dentro de esta crear la carpeta viaticos, lo demas se va creando al cargar comprobantes

## Mostrar consultas en consola

Si se desea mostrar o oculatar consultas JPA en consola, en el archivo application.properties modificar a true o false este codigo
``` properties
spring.jpa.show-sql=false
```