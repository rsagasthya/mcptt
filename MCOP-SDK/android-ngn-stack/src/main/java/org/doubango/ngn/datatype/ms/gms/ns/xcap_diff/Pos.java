/*

 *  Copyright (C) 2017, University of the Basque Country (UPV/EHU)
 *
 * Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
 *
 * This file is part of MCOP MCPTT Client
 *
 * This is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.doubango.ngn.datatype.ms.gms.ns.xcap_diff;

import org.simpleframework.xml.Root;


/**
 * <p>Clase Java para pos.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="pos">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="before"/>
 *     &lt;enumeration value="after"/>
 *     &lt;enumeration value="prepend"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@Root(strict=false, name = "pos")
public enum Pos {

    BEFORE("before"),
    AFTER("after"),
    PREPEND("prepend");
    private final String value;

    Pos(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Pos fromValue(String v) {
        for (Pos c: Pos.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
