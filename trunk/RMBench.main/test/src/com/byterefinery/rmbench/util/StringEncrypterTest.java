package com.byterefinery.rmbench.util;

import junit.framework.TestCase;

/** 
 * 
 * @author Thomas Pr&ouml;ger
 * 
 */
public class StringEncrypterTest extends TestCase
{
	
	public void testEncryptWithDefaultKey() throws Exception{
		
		String stringToEncrypt = "Hello World";
		StringEncrypter encrypter = new StringEncrypter(StringEncrypter.DESEDE_ENCRYPTION_SCHEME);
		
		String encryptedString = encrypter.encrypt(stringToEncrypt);
		
		assertEquals("f/I/LhH9wscAUBCBgzA/WA==",encryptedString);
	}

	public void testThrowsErrorOnInvalidKeySpec() throws Exception
	{
		String encryptionScheme = "asdf";
		String encryptionKey = "123456789012345678901234567890";

		try
		{
			new StringEncrypter( encryptionScheme,	encryptionKey );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "Encryption scheme not supported: asdf", e.getMessage() );
		}
	}

	public void testEncryptsUsingDesEde() throws Exception
	{
		String stringToEncrypt = "test";
		String encryptionKey = "123456789012345678901234567890";
		String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;

		StringEncrypter encrypter = new StringEncrypter( encryptionScheme, encryptionKey );
		String encryptedString = encrypter.encrypt( stringToEncrypt );

		assertEquals( "Ni2Bih3nCUU=", encryptedString );
	}

	public void testEncryptsUsingDes() throws Exception
	{
		String stringToEncrypt = "test";
		String encryptionKey = "123456789012345678901234567890";
		String encryptionScheme = StringEncrypter.DES_ENCRYPTION_SCHEME;

		StringEncrypter encrypter = new StringEncrypter( encryptionScheme, encryptionKey );
		String encryptedString = encrypter.encrypt( stringToEncrypt );

		assertEquals( "oEtoaxGK9ns=", encryptedString );
	}

	public void testEncryptionKeyCanContainLetters() throws Exception
	{
		String string = "test";
		String encryptionKey = "ASDF asdf 1234 8983 jklasdf J2Jaf8";
		String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;

		StringEncrypter encrypter = new StringEncrypter( encryptionScheme, encryptionKey );
		String encryptedString = encrypter.encrypt( string );

		assertEquals( "Q+UyPrxdge0=", encryptedString );
	}

	public void testDecryptsUsingDesEde() throws Exception
	{
		String string = "Ni2Bih3nCUU=";
		String encryptionKey = "123456789012345678901234567890";
		String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;

		StringEncrypter encrypter = new StringEncrypter( encryptionScheme, encryptionKey );
		String decryptedString = encrypter.decrypt( string );

		assertEquals( "test", decryptedString );
	}

	public void testDecryptsUsingDes() throws Exception
	{
		String string = "oEtoaxGK9ns=";
		String encryptionKey = "123456789012345678901234567890";
		String encryptionScheme = StringEncrypter.DES_ENCRYPTION_SCHEME;

		StringEncrypter encrypter = new StringEncrypter( encryptionScheme, encryptionKey );
		String decryptedString = encrypter.decrypt( string );

		assertEquals( "test", decryptedString );
	}

	public void testCantInstantiateWithNullEncryptionKey() throws Exception
	{
		try
		{
			String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
			String encryptionKey = null;

             new StringEncrypter( encryptionScheme, encryptionKey );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "encryption key was null", e.getMessage() );
		}

	}

	public void testCantInstantiateWithEmptyEncryptionKey() throws Exception
	{
		try
		{
			String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
			String encryptionKey = "";

			new StringEncrypter( encryptionScheme,	encryptionKey );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "encryption key was less than 24 characters", e.getMessage() );
		}

	}

	public void testCantDecryptWithNullString() throws Exception
	{
		try
		{
			String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
			String encryptionKey = "123456789012345678901234";

			StringEncrypter encrypter = new StringEncrypter( encryptionScheme,
					encryptionKey );
			encrypter.decrypt( null );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "encrypted string was null or empty", e.getMessage() );
		}

	}

	public void testCantDecryptWithEmptyString() throws Exception
	{
		try
		{
			String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
			String encryptionKey = "123456789012345678901234";

			StringEncrypter encrypter = new StringEncrypter( encryptionScheme,
					encryptionKey );
			encrypter.decrypt( "" );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "encrypted string was null or empty", e.getMessage() );
		}

	}

	public void testCantEncryptWithNullString() throws Exception
	{
		try
		{
			String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
			String encryptionKey = "123456789012345678901234";

			StringEncrypter encrypter = new StringEncrypter( encryptionScheme,
					encryptionKey );
			encrypter.encrypt( null );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "unencrypted string was null or empty", e.getMessage() );
		}

	}

	public void testCantEncryptWithEmptyString() throws Exception
	{
		try
		{
			String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
			String encryptionKey = "123456789012345678901234";

			StringEncrypter encrypter = new StringEncrypter( encryptionScheme,
					encryptionKey );
			encrypter.encrypt( "" );
		}
		catch (IllegalArgumentException e)
		{
			assertEquals( "unencrypted string was null or empty", e.getMessage() );
		}

	}
}