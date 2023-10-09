/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal;

import java.io.IOException;

/**
 *
 * @author Yair
 */
public class AnalizadorSintactico {
//One ring to rule them all
    private AnalizadorLexico alex;
    private IndicadorError indError;
    private AnalizadorSemantico aSem;
    private GeneradorDeCodigo genCod;
    private int cantidadDeVariables;

    public AnalizadorSintactico(AnalizadorLexico alex, IndicadorError indError, AnalizadorSemantico aSem, GeneradorDeCodigo genCod) {
        this.alex = alex;
        this.indError = indError;
        this.aSem = aSem;
        this.genCod = genCod;
    }

    public void analizar() throws IOException {
        intentoMaravilla();   
        programa();
        if (alex.getSimbolo() == Terminal.EOF) {
            indError.mostrarError(0, alex.getCadena());
        } else {
            indError.mostrarError(1, alex.getCadena());
        }
    }

    private void programa() throws IOException {
        
        genCod.cargarByte(0xBF);
        genCod.cargarInt(0);
        
        bloque(0);
        if (alex.getSimbolo() == Terminal.PUNTO) {
            int distancia = 1416 - (genCod.getTopeMemoria() + 5);
            genCod.cargarByte(0xE9);
            genCod.cargarInt(distancia);
            //genCod.cargarIntEn(,1793); // IMAGE BASE + BASE OF CODE + TOPE MEMORIA - HEADER SIZE
            for(int i = 0; i < cantidadDeVariables;i++){
                genCod.cargarInt(0);
            }
           //genCod.cargarIntEn(genCod.getTopeMemoria() - , 416); //TOPE MEMORIA - EL TAMAÑO DEL HEADER
           // DESPUÉS HAY QUE RELLENAR EL ARCHIVO CON 0s HASTA QUE SEA MULTIPLO DEL ENTERO QUE SE ENCUENTRA EN LOS BYTES 216 A 219
           // while topememoria % file alignment != 0
           // genCod.cargarByte(0x00);
           // genCod.cargarByteEn(genCod.getTopeMemoria() - , 188); //TOPE MEMORIA - EL TAMAÑO DEL HEADER
           // genCod.cargarByteEn(genCod.getTopeMemoria() - , 424); //TOPE MEMORIA - EL TAMAÑO DEL HEADER
           // genCod.cargarIntEn((2 + sizeOfCodeSection / sectionAlignment) * sectionAlignment), 240);
           // genCod.cargarIntEn((2 + sizeOfRawData / sectionAlignment) * sectionAlignment), 208);
            intentoMaravilla();
        } else {
            indError.mostrarError(2, alex.getCadena());          
        }
    }

    private void bloque(int base) throws IOException {
        genCod.cargarByte(0xE9);
        genCod.cargarInt(0); // PENDIENTE DE FIX UPEAR
        int desplazamiento = 0;
        int indice;
        if (alex.getSimbolo() == Terminal.CONST) {
            
            IdentificadorBean beanConstante = new IdentificadorBean();
            beanConstante.setTipo(alex.getSimbolo());
            intentoMaravilla();
        
            if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                beanConstante.setNombre(alex.getCadena());
                intentoMaravilla();
            } else {
                indError.mostrarError(3, alex.getCadena());
            }
            
            if (alex.getSimbolo() == Terminal.IGUAL) {
                intentoMaravilla();
            } else {
                indError.mostrarError(4, alex.getCadena());
            }
            
            if (alex.getSimbolo() == Terminal.NUMERO) {
                beanConstante.setValor(Integer.parseInt(alex.getCadena()));
                
                indice = aSem.buscarBean(base+desplazamiento-1, base, beanConstante.getNombre());
                if(indice == -1){
                    aSem.agregarBean(base+desplazamiento, beanConstante); 
                    desplazamiento++;
                    
                }else{
                    indError.mostrarError(23, alex.getCadena());
             
                }
                intentoMaravilla();                
            } else {
                indError.mostrarError(5, alex.getCadena());
            }
            while (alex.getSimbolo() == Terminal.COMA) {
                IdentificadorBean beanConstanteWhile = new IdentificadorBean();
                beanConstanteWhile.setTipo(Terminal.CONST);
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    beanConstanteWhile.setNombre(alex.getCadena());
                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
                if (alex.getSimbolo() == Terminal.IGUAL) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(4, alex.getCadena());
                }
                if (alex.getSimbolo() == Terminal.NUMERO) {
                    beanConstanteWhile.setValor(Integer.parseInt(alex.getCadena()));
                    indice = aSem.buscarBean(base+desplazamiento-1, base, beanConstanteWhile.getNombre());
                    if(indice == -1){
                        aSem.agregarBean(base+desplazamiento, beanConstanteWhile); 
                        desplazamiento++;
                    }else{
                        indError.mostrarError(23, alex.getCadena());
                    }
                    intentoMaravilla();
                } else {
                    indError.mostrarError(5, alex.getCadena());
                }
            }
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
        }
        if (alex.getSimbolo() == Terminal.VAR) {
            IdentificadorBean beanVariable = new IdentificadorBean();
            beanVariable.setTipo(alex.getSimbolo());
            intentoMaravilla();            
            if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                beanVariable.setNombre(alex.getCadena());
                beanVariable.setValor(0);
                
                indice = aSem.buscarBean(base+desplazamiento-1, base, beanVariable.getNombre());
                if(indice == -1){
                        aSem.agregarBean(base+desplazamiento, beanVariable); 
                        desplazamiento++;
                        cantidadDeVariables++;
                    }else{
                        indError.mostrarError(23, alex.getCadena());
                    }
                intentoMaravilla();
            } else {
                indError.mostrarError(3, alex.getCadena());
            }
            while (alex.getSimbolo() == Terminal.COMA) {
                IdentificadorBean beanVariableWhile = new IdentificadorBean();
                beanVariableWhile.setTipo(Terminal.VAR);
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    beanVariableWhile.setNombre(alex.getCadena());
                    beanVariableWhile.setValor(0);
                    
                    indice = aSem.buscarBean(base+desplazamiento-1, base, beanVariableWhile.getNombre());
                    if(indice == -1){
                        aSem.agregarBean(base+desplazamiento, beanVariableWhile);
                        desplazamiento++;
                        cantidadDeVariables++;
                    }else{
                        indError.mostrarError(23, alex.getCadena());
                    }
                        
                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
            }
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
        }
        while (alex.getSimbolo() == Terminal.PROCEDURE) {
            IdentificadorBean procedureBean = new IdentificadorBean();
            procedureBean.setTipo(Terminal.PROCEDURE);
            intentoMaravilla();                     
            if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                procedureBean.setNombre(alex.getCadena());
                indice = aSem.buscarBean(base+desplazamiento-1, base, procedureBean.getNombre());
                if(indice == -1){
                    aSem.agregarBean(base+desplazamiento, procedureBean);
                    desplazamiento++;
                }else{
                    indError.mostrarError(23, alex.getCadena());
                }
                intentoMaravilla();
            } else {
                indError.mostrarError(3, alex.getCadena());
            }
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {    
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
            bloque(base + desplazamiento);       
            genCod.cargarByte(0xC3); // esto es un RET
            
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
        }
        proposicion(base, desplazamiento);
    }

    private void proposicion(int base, int desplazamiento) throws IOException {
        int indice;
        switch (alex.getSimbolo()) {
            case IDENTIFICADOR:
                indice = aSem.buscarBean(base+desplazamiento-1, 0, alex.getCadena());
                if(indice == -1){
                    indError.mostrarError(24, alex.getCadena());
                }
                intentoMaravilla();
                
                if (alex.getSimbolo() == Terminal.ASIGNACION_DE_VARIABLE) {
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                } else {
                    indError.mostrarError(7, alex.getCadena());
                }
                break;
            case CALL:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    // BUSCAR IDENTIFICADOR
                    indice = aSem.buscarBean(base+desplazamiento-1, 0, alex.getCadena());
                    
                    if(indice == -1){
                        indError.mostrarError(24, alex.getCadena());
                    }else{
                        IdentificadorBean beanProc = aSem.obtenerBean(indice);
                        if(beanProc.getTipo() != Terminal.PROCEDURE){
                            indError.mostrarError(26, alex.getCadena());
                        }
                    }
                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
                break;
            case BEGIN:
                intentoMaravilla();
                proposicion(base, desplazamiento);
                while (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                    intentoMaravilla();
                    proposicion(base, desplazamiento);
                }
                if (alex.getSimbolo() == Terminal.END) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(8, alex.getCadena());
                }
                break;
            case IF:
                intentoMaravilla();
                condicion(base, desplazamiento);
                if (alex.getSimbolo() == Terminal.THEN) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(9, alex.getCadena());
                }
                proposicion(base, desplazamiento);
                break;
            case WHILE:
                intentoMaravilla();
                condicion(base, desplazamiento);
                if (alex.getSimbolo() == Terminal.DO) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(10, alex.getCadena());
                }
                proposicion(base, desplazamiento);
                break;
            case READLN:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(11, alex.getCadena());
                }
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    indice = aSem.buscarBean(base+desplazamiento-1, 0, alex.getCadena());
                    IdentificadorBean readlnBean = aSem.obtenerBean(indice);
                    if(indice == -1){
                        indError.mostrarError(24, alex.getCadena());
                    } else if(readlnBean.getTipo() == Terminal.PROCEDURE){
                        indError.mostrarError(25, alex.getCadena());
                    }
                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
                while (alex.getSimbolo() == Terminal.COMA) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                        indice = aSem.buscarBean(base+desplazamiento-1, 0, alex.getCadena());
                        IdentificadorBean readlnWhileBean = aSem.obtenerBean(indice);
                        
                        if(indice == -1){
                            indError.mostrarError(24, alex.getCadena());
                        }else if(readlnWhileBean.getTipo() == Terminal.PROCEDURE){
                            indError.mostrarError(25, alex.getCadena());
                        }
                        intentoMaravilla();
                    } else {
                        indError.mostrarError(3, alex.getCadena());
                    }
                }
                if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(12, alex.getCadena());
                }
                break;
            case WRITELN:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                        intentoMaravilla();
                    } else {
                        expresion(base, desplazamiento);
                    }
                    while (alex.getSimbolo() == Terminal.COMA) {
                        intentoMaravilla();
                        if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                            intentoMaravilla();
                        } else { 
                            expresion(base, desplazamiento); 
                        }
                    }
                    if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                        intentoMaravilla();
                    } else {
                        indError.mostrarError(12, alex.getCadena());
                    }
                }                
                break;
            case WRITE:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                }
                if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                    intentoMaravilla();
                } else {
                    expresion(base, desplazamiento);
                }
                while (alex.getSimbolo() == Terminal.COMA) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                        intentoMaravilla();
                    } else {
                        expresion(base, desplazamiento);
                    }
                }
                if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(12, alex.getCadena());
                }
                break;
        }
    }

    private void expresion(int base, int desplazamiento) throws IOException {
        boolean esPositivo = true;
        if (alex.getSimbolo() == Terminal.OPERADOR_SUMA) {
            esPositivo = true;
            intentoMaravilla();
        } else if (alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
            esPositivo = false;
            intentoMaravilla();
        }
        termino(base, desplazamiento);
        if(!esPositivo){
            genCod.cargarByte(0x58); // POP EAX
            genCod.cargarByte(0xF7);
            genCod.cargarByte(0xD8); // NEG EAX
            genCod.cargarByte(0x50); // PUSH EAX
        }
        
        while (alex.getSimbolo() == Terminal.OPERADOR_SUMA || alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
            boolean operadorFlag=false;
            if (alex.getSimbolo() == Terminal.OPERADOR_SUMA){
                operadorFlag = true;
            } else if(alex.getSimbolo() == Terminal.OPERADOR_RESTA){
                operadorFlag = false;
            }
            intentoMaravilla();
            termino(base, desplazamiento);
            genCod.cargarByte(0x58); // POP EAX
            genCod.cargarByte(0x5B); // POP EBX
            
            if(operadorFlag){
                genCod.cargarByte(0x01); 
                genCod.cargarByte(0xD8); // ADD EAX, ABX
            }else{
                genCod.cargarByte(0x93);
                genCod.cargarByte(0x29);
                genCod.cargarByte(0xD8);
            }
            genCod.cargarByte(0x50); // PUSH EAX
        }
    }

    private void condicion(int base, int desplazamiento) throws IOException {
        if (alex.getSimbolo() == Terminal.ODD) {
            intentoMaravilla();
            expresion(base, desplazamiento);
        } else {
            expresion(base, desplazamiento);
            switch (alex.getSimbolo()) {
                case IGUAL:
                case DISTINTO:
                case MENOR:
                case MENOR_O_IGUAL:
                case MAYOR:
                case MAYOR_O_IGUAL:
                    intentoMaravilla();
                    break;
                default:
                    indError.mostrarError(19, alex.getCadena());
            }
            expresion(base, desplazamiento);
        }
    }

    private void termino(int base, int desplazamiento) throws IOException {
        factor(base, desplazamiento);
        while (alex.getSimbolo() == Terminal.OPERADOR_MULTIPLICACION || alex.getSimbolo() == Terminal.OPERADOR_DIVISION) {
            boolean esMultiplicacion = true;
            if(alex.getSimbolo() == Terminal.OPERADOR_MULTIPLICACION){
                esMultiplicacion = true;
            }else if (alex.getSimbolo() == Terminal.OPERADOR_DIVISION){
                esMultiplicacion = false;
            }
            intentoMaravilla();
            factor(base, desplazamiento);
            if(esMultiplicacion){
                genCod.cargarByte(0x58); // POP EAX
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0xF7);
                genCod.cargarByte(0xEB); // IMUL EBX
                genCod.cargarByte(0x50); // PUSH EAX
            } else if (!esMultiplicacion){
                genCod.cargarByte(0x58); // POP EAX
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0x93); 
                genCod.cargarByte(0x99); 
                genCod.cargarByte(0xF7); 
                genCod.cargarByte(0xFB); 
                genCod.cargarByte(0x50); // PUSH EAX
            }
        }
    }

    private void factor(int base, int desplazamiento) throws IOException {
        switch (alex.getSimbolo()){
            case IDENTIFICADOR:
                // HACER ANALIZADOR SEMANTICO 
                // SI ES VAR HACERLO DE ESTA FORMA
                IdentificadorBean beanFactor;
                int indice = aSem.buscarBean(base+desplazamiento-1, 0, alex.getCadena());

                beanFactor = aSem.obtenerBean(indice);
                
                if(beanFactor == null){
                    indError.mostrarError(24, alex.getCadena());
                }
                
                if(beanFactor.getTipo().equals(Terminal.VAR)){
                    genCod.cargarByte(0xB8);
                    genCod.cargarByte(0x87);
                    genCod.cargarInt(beanFactor.getValor());
                    genCod.cargarByte(0x50);
                } else if (beanFactor.getTipo().equals(Terminal.CONST)){
                    // SI ES CONST HACERLO DE LA OTRA FORMA
                }else{
                    indError.mostrarError(25, alex.getCadena());
                }
                intentoMaravilla();
                break;
            case NUMERO:
                genCod.cargarByte(0xB8);
//                genCod.cargarInt(Integer.parseInt(alex.getCadena()));
                genCod.cargarByte(0x50);
                intentoMaravilla();
                break;
            case APERTURA_PARENTESIS:
                intentoMaravilla();
                expresion(base, desplazamiento);
                if(alex.getSimbolo()==Terminal.CIERRE_PARENTESIS){
                    intentoMaravilla();
                }else{
                    indError.mostrarError(22, alex.getCadena());
                }
                break;
            default:    
                indError.mostrarError(20, alex.getCadena());    
        }   
    }
    
    private void intentoMaravilla() throws IOException{
        do {
            alex.escanear();
        }while(alex.getCadena().isBlank());
    }
}
