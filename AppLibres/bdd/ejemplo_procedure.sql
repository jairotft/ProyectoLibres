CREATE OR REPLACE FUNCTION registrarCliente (id varchar,nomb varchar,direcc varchar,email varchar) RETURNS void AS $$
    BEGIN
        insert into Cliente values($1,$2,$3,$4);          
    END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION registrarEstablecimiento (id char,nomb varchar,direcc varchar) RETURNS void AS $$
    BEGIN
        insert into Establecimiento values($1,$2,$3);          
    END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION registrarFactura (id integer,id_c varchar, id_est char,fecha timestamp without time zone, estado varchar,ambiente varchar,total_sin money,iva money, valor_total money) RETURNS void AS $$
    BEGIN
        insert into Factura values($1,$2,$3,$4,$5,$6,$7,$8,$9);          
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION registrarProducto (id int,descri varchar) RETURNS void AS $$
    BEGIN
        insert into Producto values($1,$2);          
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION registrarDetalle (id_pro int,id_fact int, total money, cant money, prec_uni money RETURNS void AS $$
    BEGIN
        insert into Detalle values($1,$2,$3,$4,$5);          
    END;
$$ LANGUAGE plpgsql;

select * from cliente
select * from establecimiento
select * from factura
select *from producto
select *from detalle


select registrarCliente('1718269671','Juan','Pifo','juan@epn.edu.ec')
select registrarEstablecimiento('8375928473001','Supermaxi','Cumbaya')
select registrarFactura(1456,'1718269671', '8375928473001',current_date, 'Normal','Normal',12.5,2, 14.5)

insert into Factura values(1,'1718269671', '8375928473001','2016-12-20', 'Normal','Normal',12.5,2, 14.5)

select borrarCliente('1718269671')

CREATE OR REPLACE FUNCTION borrarCliente (id varchar) RETURNS void AS $$
    BEGIN
        delete from tipo_gasto where id_factura in (select id_factura from factura where id_cliente=$1);
        delete from factura where id_cliente=$1;
        delete from cliente where id_cliente=$1;
    END;
$$ LANGUAGE plpgsql;


