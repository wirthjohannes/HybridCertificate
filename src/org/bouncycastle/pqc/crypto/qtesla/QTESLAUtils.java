package org.bouncycastle.pqc.crypto.qtesla;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.io.*;
import java.util.Arrays;

public class QTESLAUtils {

    public static final String OID_HEURISTIC_I = "1.3.6.1.4.1.311.89.2.2.1";
    public static final String OID_HEURISTIC_III_SIZE = "1.3.6.1.4.1.311.89.2.2.2";
    public static final String OID_HEURISTIC_III_SPEED = "1.3.6.1.4.1.311.89.2.2.3";

    public static ASN1Primitive toASN1Primitive(QTESLAPublicKeyParameters key) {
        return new DERBitString(key.getPublicData());
    }

    public static ASN1Primitive toASN1Primitive(QTESLAPrivateKeyParameters key) {
        return new DERBitString(key.getSecret());
    }

    public static QTESLAPublicKeyParameters fromASN1Primitive(byte[] data, int securityCategory) {
        return new QTESLAPublicKeyParameters(securityCategory, data);
    }

    public static QTESLAPrivateKeyParameters fromASN1PrimitivePrivate(byte[] data, int securityCategory) throws IOException {
        ASN1BitString seq = (ASN1BitString) ASN1Primitive.fromByteArray(data);
        return new QTESLAPrivateKeyParameters(securityCategory, seq.getBytes());
    }

    public static QTESLAPublicKeyParameters fromSubjectPublicKeyInfo(SubjectPublicKeyInfo key) {
        try {
            byte[] data = key.getPublicKeyData().getEncoded();;
            ASN1BitString asn1 = (ASN1BitString) ASN1BitString.fromByteArray(data);
            return fromASN1Primitive(asn1.getBytes(), getSecurityCategory(key.getAlgorithm()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getSignatureSize(int securityCategory) {
        return QTESLASecurityCategory.getSignatureSize(securityCategory);
    }

    public static boolean isQTESLA(AlgorithmIdentifier algId) {
        String oid = algId.getAlgorithm().getId();
        return oid.equals(OID_HEURISTIC_I) || oid.equals(OID_HEURISTIC_III_SIZE) || oid.equals(OID_HEURISTIC_III_SPEED);
    }

    public static int getSecurityCategory(AlgorithmIdentifier algId) {
        switch (algId.getAlgorithm().getId()) {
            case OID_HEURISTIC_I:
                return QTESLASecurityCategory.HEURISTIC_I;
            case OID_HEURISTIC_III_SIZE:
                return QTESLASecurityCategory.HEURISTIC_III_SIZE;
            case OID_HEURISTIC_III_SPEED:
                return QTESLASecurityCategory.HEURISTIC_III_SPEED;
            default:
                return -1;
        }
    }

    public static String getOID(int securityCategory) {
        switch (securityCategory) {
            case QTESLASecurityCategory.HEURISTIC_I:
                return OID_HEURISTIC_I;
            case QTESLASecurityCategory.HEURISTIC_III_SIZE:
                return OID_HEURISTIC_III_SIZE;
            case QTESLASecurityCategory.HEURISTIC_III_SPEED:
                return OID_HEURISTIC_III_SPEED;
            default:
                return "";
        }
    }

    public static AlgorithmIdentifier getAlgorithmIdentifier(int securityCategory) {
        return new AlgorithmIdentifier(new ASN1ObjectIdentifier(getOID(securityCategory)));
    }

}
