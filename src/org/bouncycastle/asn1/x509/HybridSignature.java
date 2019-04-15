package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.IOException;
import java.security.cert.X509Certificate;

public class HybridSignature extends ASN1Object {

    public static final String OID = "2.5.29.212";
    private byte[] signature;
    private AlgorithmIdentifier algId;

    /**
     * Create a new HybridSignature-Extension
     *
     * @param signature the signature
     * @param algId the AlgId of the signature
     */
    public HybridSignature(byte[] signature, AlgorithmIdentifier algId) {
        this.signature = signature;
        this.algId = algId;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(algId);
        v.add(new DERBitString(signature));
        return new DERSequence(v);
    }

    /**
     * Query the signature from the extension
     *
     * @return the signature bytes
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Query the AlgId from the extension
     *
     * @return the AlgId of the signature
     */
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return algId;
    }

    /**
     * Extract the HybridSignature-Extension from a certificate
     *
     * @param cert the certificate
     * @return the HybridSignature-Extension
     */
    public static HybridSignature fromCert(X509Certificate cert) throws IOException {
        byte[] data = cert.getExtensionValue(OID);
        ASN1InputStream input = new ASN1InputStream(data);
        ASN1OctetString octstr = ASN1OctetString.getInstance(input.readObject());
        ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(octstr.getOctets());
        AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        ASN1BitString sig = (ASN1BitString) seq.getObjectAt(1);
        return new HybridSignature(sig.getOctets(), algId);
    }

    /**
     * Extract the HybridSignature-Extension from a CSR
     *
     * @param csr the CSR
     * @return the HybridSignature-Extension
     */
    public static HybridSignature fromCSR(PKCS10CertificationRequest csr) throws IOException {
        org.bouncycastle.asn1.pkcs.Attribute[] attr = csr.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);
        if (attr.length > 0) {
            // System.out.println(Arrays.toString(attr[0].getAttributeValues()));
            ASN1Encodable[] encodable = attr[0].getAttributeValues();
            // System.out.println(encodable[0]);
            Extensions ext = Extensions.getInstance(encodable[0]);

            byte[] data = ext.getExtension(new ASN1ObjectIdentifier(OID)).getExtnValue().getEncoded();
            ASN1InputStream input = new ASN1InputStream(data);
            ASN1OctetString octstr = ASN1OctetString.getInstance(input.readObject());
            ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(octstr.getOctets());
            AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
            ASN1BitString sig = (ASN1BitString) seq.getObjectAt(1);
            return new HybridSignature(sig.getOctets(), algId);

        } else

            return null;
    }
}
