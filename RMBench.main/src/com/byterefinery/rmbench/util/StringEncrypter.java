/*
 * created 15.09.2005 by thomasp
 *
 * Copyright 2006, DynaBEAN Consulting
 * 
 * $Id$
 */
package com.byterefinery.rmbench.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;


/**
 * This Class helps to en/decrypt strings using the Java Cryptography Extension (JCE). It
 * has a very simple structure and after initializing a <code>StringEncrypter</code> object
 * with a specific encryption scheme you can use the methods <code>encrypt()</code> and 
 * <code>decrypt()</code> to encrypt (decrypt) a given string literal. Be aware to use the same
 * <code>StringEncrypter</code> instance for encrypting and decrypting a particular string, since
 * two StringEncrypter can use different schemes or keys for encryption<br>
 * <br><br>
 * If no key is given to the constructor, a default key with a 40 characters length will be 
 * used. All keys must be at least 24 characters long.
 * 
 * @author Thomas Pr&ouml;ger
 */
public class StringEncrypter
{
	
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	public static final String DEFAULT_ENCRYPTION_KEY = "123wfsgSWwwwww234%�Ggsdf23413ACSFAD!Aaa";
	
	private KeySpec				keySpec;
	private SecretKeyFactory	keyFactory;
	private Cipher				cipher;
	
	private static final String	UNICODE_FORMAT = "UTF-8";
    
    private final byte base64EncodingTable[] = { 
               'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
               'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
               'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
               'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
               'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
               't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
               '2', '3', '4', '5', '6', '7', '8', '9', '+',
               '/' };

	/**
	 * Creates a <code>StringEncrypter</code> object using the given encryption scheme
	 * and the default key. 
	 * 
	 * @param encryptionScheme
	 * @throws EncryptionException This Exception is thrown when the given encryption scheme
	 * is not recoginized to be supported.
	 */
	public StringEncrypter( String encryptionScheme ) throws EncryptionException
	{
		this( encryptionScheme, DEFAULT_ENCRYPTION_KEY );
	}

	/**
	 * Creates a <code>StringEncrypter</code> object using a user given encryption scheme
	 * and encryption key.
	 * 
	 * @param encryptionScheme
	 * @param encryptionKey
     * @throws EncryptionException This Exception is thrown when either the encryption key is 
     * <code>null</code>, the key is less than 24 characters long or the given encryption scheme
     * is not recoginized to be supported.
	 */
	public StringEncrypter( String encryptionScheme, String encryptionKey )
			throws EncryptionException
	{

		if ( encryptionKey == null )
				throw new IllegalArgumentException( "encryption key was null" );
		if ( encryptionKey.trim().length() < 24 )
				throw new IllegalArgumentException(
						"encryption key was less than 24 characters" );

		try
		{
			byte[] keyAsBytes = encryptionKey.getBytes( UNICODE_FORMAT );

			if ( encryptionScheme.equals( DESEDE_ENCRYPTION_SCHEME) )
			{
				keySpec = new DESedeKeySpec( keyAsBytes );
			}
			else if ( encryptionScheme.equals( DES_ENCRYPTION_SCHEME ) )
			{
				keySpec = new DESKeySpec( keyAsBytes );
			}
			else
			{
				throw new EncryptionException( "Encryption scheme not supported: "
													+ encryptionScheme );
			}

			keyFactory = SecretKeyFactory.getInstance( encryptionScheme );
			cipher = Cipher.getInstance( encryptionScheme );

		}
		catch (InvalidKeyException e)
		{
			throw new EncryptionException( e );
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchPaddingException e)
		{
			throw new EncryptionException( e );
		}
	}

	/**
	 * Encrypts the given string according to the encryption scheme and key assoziated with
	 * this object. 
	 * 
	 * @param unencryptedString
	 * @return
	 * @throws EncryptionException
	 */
	public String encrypt( String unencryptedString ) throws EncryptionException
	{
		if ( unencryptedString == null || unencryptedString.trim().length() == 0 )
				throw new IllegalArgumentException(
						"unencrypted string was null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.ENCRYPT_MODE, key );
			byte[] cleartext = unencryptedString.getBytes( UNICODE_FORMAT );
			byte[] ciphertext = cipher.doFinal( cleartext );

			//BASE64Encoder base64encoder = new BASE64Encoder();
            
            return getBase64Encoding(ciphertext);
            
			//return base64encoder.encode( ciphertext );			
		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}

	/**
	 * Decrypts a previously encrypted string into its original character sequence. The string
	 * must be encrypted using the method <code>encrypt</code> of this <code>StringEncrypter</code> 
	 * object.  
	 * 
	 * @param encryptedString
	 * @return 
	 * @throws EncryptionException
	 */
	public String decrypt( String encryptedString ) throws EncryptionException
	{
		if ( encryptedString == null || encryptedString.trim().length() <= 0 )
				throw new IllegalArgumentException( "encrypted string was null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.DECRYPT_MODE, key );
			byte[] cleartext = getBase64Decoding(encryptedString);			
			byte[] ciphertext = cipher.doFinal( cleartext );
            
            return new String(ciphertext, UNICODE_FORMAT);
		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}

    public static class EncryptionException extends Exception
	{

        private static final long serialVersionUID = -3146108954137380354L;

        public EncryptionException( Throwable t )
		{
			super( t );
		}

        public EncryptionException(String message) {
            super(message);
        }
	}
    
    /**
     * this class takes a base64 encoded string and returns a decoded byte array
     * @param string
     * @return
     */
    private byte[] getBase64Decoding(String string) {
		byte[] input = string.getBytes();
		byte[] output = new byte[input.length];
		int outOff = 0;
		int i;
		for (i = 0; i < input.length; i++) {

			int index = getIndex(input[i]);
			if (index == -1)
				break;

			output[outOff] = (byte) (index << 2);
			i++;
			index = getIndex(input[i]);
			if (index == -1)
				break;

			output[outOff] += (byte) (index >> 4);
			outOff++;
			// first byte finished

			output[outOff] = (byte) ((index & 15) << 4);
			i++;
			index = getIndex(input[i]);
			if (index == -1)
				break;

			output[outOff] += (byte) ((index & 60) >> 2);
			outOff++;
			// second byte finished

			output[outOff] = (byte) ((index & 3) << 6);
			i++;
			index = getIndex(input[i]);
			if (index == -1)
				break;

			output[outOff] += (byte) (index & 63);
			outOff++;
			// well third byte finished :)
		}
		byte[] result = new byte[outOff];
		System.arraycopy(output, 0, result, 0, outOff);

		return result;
	}
    
    
    /**
	 * Returns the index of a byte in the base&$LookUpTable.
	 * 
	 * @param b
	 *            the byte to search
	 * @return the index of the byte
	 */
    private int getIndex(byte b) {
        int i=0;
        for (i=0; i<base64EncodingTable.length; i++) {
            if (b==base64EncodingTable[i])
                return i;
        }
        return -1;
    }
    
    /**
	 * This class encodes a given byte array into base64 and returns the
	 * resulting String
	 * 
	 * @param input
	 * @return
	 * @throws EncryptionException
	 */
    private String getBase64Encoding(byte[] input) throws EncryptionException {
        byte[] output = new byte[(int) (input.length*1.5)];
        int outOffset = 0;
        for (int i=0; i<input.length; i++) {
            int tmp = 0;
           
            // take first 6 bit of unsigned converted byte
            tmp = ((int) 0xFF & input[i]) >> 2;
            
            output[outOffset] = base64EncodingTable[tmp];
            outOffset++;
            
            // get last 2 bits of first character  
            tmp = (((int) 0xFF & input[i])&3) << 4;
            i++;
            if (i<input.length) {
                // add higher 4 bit of 2. byte
                tmp += ((int) 0xFF & input[i])>>4;
            }
            output[outOffset] = base64EncodingTable[tmp];
            outOffset++;
            //get last 4 bit of 2. byte or if i>length print 2 '='
            if (i<input.length) {
                tmp = (((int) 0xFF & input[i])&15)<<2;
                i++;
                if (i<input.length) {
                    // add higher 4 bit of 2. byte
                    tmp += ((int) 0xFF & input[i])>>6;
                }
                output[outOffset] = base64EncodingTable[tmp];
                outOffset++;
                
                // check if we have a last byte
                if (i<input.length) {
                    tmp = ((int) 0xFF & input[i])&63;
                    output[outOffset] = base64EncodingTable[tmp];
                } else {
                    output[outOffset] = '=';
                }
                outOffset++;
            } else {
                output[outOffset] = '=';
                outOffset++;
                output[outOffset] = '=';
                outOffset++;
            }
        } 
        
        try {
            return new String(output, 0, outOffset, UNICODE_FORMAT);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncryptionException(e);
        }
    }
}