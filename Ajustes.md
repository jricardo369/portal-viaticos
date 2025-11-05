# AJUSTES PORTAL VIATICS

## AJUSTE NUEVO NIVEL APROBACION GERENTE
Se agrego nueva tabla de de empresa_aprobacion con id_empresa_aprobacion,empresa,codigo
Se agrego en el metodo autorizarComprobacion en AprobacionesService, la logica para en caso de tener el rfc el usuario enviar a estatus 16
