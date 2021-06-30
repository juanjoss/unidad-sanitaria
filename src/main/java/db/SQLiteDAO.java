package db;

import org.sql2o.Sql2o;

public class SQLiteDAO {

    private static Sql2o sql2o;

    public static Sql2o getConn() {
        if (sql2o == null) {
            sql2o = new Sql2o("jdbc:sqlite:./unidad_sanitaria.db", null, null);

            String queryEquipoTable = "CREATE TABLE IF NOT EXISTS 'equipoMedico' ("
                + "'nombre'	TEXT NOT NULL UNIQUE,"
                + "'id'	INTEGER NOT NULL,"
                + "'stock'	INTEGER NOT NULL,"
                + "PRIMARY KEY('id' AUTOINCREMENT));";

            String queryPresentacionTable = "CREATE TABLE IF NOT EXISTS 'presentacion' ("
                + "'id'	INTEGER NOT NULL,"
                + "'nombre'	TEXT NOT NULL UNIQUE,"
                + "PRIMARY KEY('id'));";

            String queryMedicamentoTable = "CREATE TABLE IF NOT EXISTS 'medicamento' ("
                + "'id'	INTEGER NOT NULL,"
                + "'nombre'	TEXT NOT NULL,"
                + "'stock'	INTEGER,"
                + "'fechaVencimiento'	TEXT NOT NULL,"
                + "'laboratorio'	TEXT NOT NULL,"
                + "'dosis'	TEXT NOT NULL,"
                + "'id_presentacion'	INTEGER NOT NULL,"
                + "PRIMARY KEY('id' AUTOINCREMENT),"
                + "FOREIGN KEY('id_presentacion') REFERENCES 'presentacion'('id'));";

            String queryUsuarioTable = "CREATE TABLE IF NOT EXISTS 'usuario' ("
                + "'id'	INTEGER NOT NULL,"
                + "'pass'	TEXT NOT NULL,"
                + "'userName'	TEXT NOT NULL UNIQUE,"
                + "'ultimaSesion'	TEXT,"
                + "'email'	TEXT NOT NULL,"
                + "PRIMARY KEY('id' AUTOINCREMENT));";

            String queryPedidoTable = "CREATE TABLE IF NOT EXISTS 'pedido' ("
                + "'id'	INTEGER,"
                + "'idUsuario'	INTEGER NOT NULL,"
                + "'correoProveedor'	TEXT NOT NULL,"
                + "'fecha'	TEXT NOT NULL,"
                + "'estado'	TEXT NOT NULL,"
                + "FOREIGN KEY('idUsuario') REFERENCES 'usuario'('id'),"
                + "PRIMARY KEY('id' AUTOINCREMENT));";

            String queryDetallePedidoEMTable = "CREATE TABLE IF NOT EXISTS 'detallePedidoEM' ("
                + "'id'	INTEGER,"
                + "'cantidad'	INTEGER NOT NULL,"
                + "'descripcion'	TEXT,"
                + "'pedido_id'	INTEGER NOT NULL,"
                + "'equipoMedico_id'	INTEGER NOT NULL,"
                + "FOREIGN KEY('equipoMedico_id') REFERENCES 'equipoMedico'('id'),"
                + "FOREIGN KEY('pedido_id') REFERENCES 'pedido'('id'),"
                + "PRIMARY KEY('id' AUTOINCREMENT));";

            String queryDetallePedidoMTable = "CREATE TABLE IF NOT EXISTS 'detallePedidoM' ("
                + "'id'	INTEGER,"
                + "'cantidad'	INTEGER NOT NULL,"
                + "'descripcion'	TEXT,"
                + "'medicamento_id'	INTEGER NOT NULL,"
                + "'pedido_id'	INTEGER NOT NULL,"
                + "FOREIGN KEY('medicamento_id') REFERENCES 'medicamento'('id'),"
                + "FOREIGN KEY('pedido_id') REFERENCES 'pedido'('id'),"
                + "PRIMARY KEY('id' AUTOINCREMENT));";

            String queryDataPresentacion = "INSERT OR IGNORE INTO presentacion(nombre) VALUES ('Sin Especificar');";
            String queryUser = "INSERT OR IGNORE INTO usuario('id', 'pass', 'userName', 'ultimaSesion', 'email') VALUES ('0', 'admin', 'Administrador', 'today', 'unisancolser@hotmail.com.ar');";
            try {
                sql2o.open().createQuery(queryEquipoTable).executeUpdate();
                sql2o.open().createQuery(queryPresentacionTable).executeUpdate();
                sql2o.open().createQuery(queryMedicamentoTable).executeUpdate();
                sql2o.open().createQuery(queryUsuarioTable).executeUpdate();
                sql2o.open().createQuery(queryPedidoTable).executeUpdate();
                sql2o.open().createQuery(queryDetallePedidoEMTable).executeUpdate();
                sql2o.open().createQuery(queryDetallePedidoMTable).executeUpdate();
                sql2o.open().createQuery(queryDataPresentacion).executeUpdate();
                sql2o.open().createQuery(queryUser).executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return sql2o;
    }
}
