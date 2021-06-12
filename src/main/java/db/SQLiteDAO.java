package db;

import org.sql2o.Sql2o;

public class SQLiteDAO {

    private static Sql2o sql2o;

    public static Sql2o getConn() {
        if (sql2o == null) {
            sql2o = new Sql2o("jdbc:sqlite:./unidad_sanitaria.db", null, null);

            String queryEquipoTable = "CREATE TABLE IF NOT EXISTS 'equipoMedico' ("
                + "'nombre'	TEXT NOT NULL,"
                + "'id'	INTEGER NOT NULL,"
                + "'stock'	INTEGER NOT NULL,"
                + "PRIMARY KEY('id','nombre'));";

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
                + "PRIMARY KEY('id' AUTOINCREMENT));";
            
            String queryPedidoTable = "CREATE TABLE IF NOT EXISTS 'pedido' ("
                + "'id'	INTEGER,"
                + "'idUsuario'	INTEGER NOT NULL,"
                + "'correoProveedor'	INTEGER NOT NULL,"
                + "'fecha'	TEXT NOT NULL,"
                + "'estado'	TEXT NOT NULL,"
                + "FOREIGN KEY('idUsuario') REFERENCES 'usuario'('id'),"
                + "PRIMARY KEY('id'));";
            
            String queryDetallePedidoTable = "CREATE TABLE IF NOT EXISTS 'detallePedido' ("
                + "'id'	INTEGER NOT NULL,"
                + "'idPedido'	INTEGER NOT NULL,"
                + "'cantidad'	INTEGER NOT NULL,"
                + "'descripcion'	TEXT,"
                + "FOREIGN KEY('idPedido') REFERENCES 'pedido'('id'),"
                + "PRIMARY KEY('id' AUTOINCREMENT));";

            String queryDataPresentacion = "INSERT OR IGNORE INTO presentacion(nombre) VALUES ('Sin Especificar');";

            try {
                sql2o.open().createQuery(queryEquipoTable).executeUpdate();
                sql2o.open().createQuery(queryPresentacionTable).executeUpdate();
                sql2o.open().createQuery(queryMedicamentoTable).executeUpdate();
                sql2o.open().createQuery(queryUsuarioTable).executeUpdate();
                sql2o.open().createQuery(queryPedidoTable).executeUpdate();
                sql2o.open().createQuery(queryDetallePedidoTable).executeUpdate();
                sql2o.open().createQuery(queryDataPresentacion).executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return sql2o;
    }
}
