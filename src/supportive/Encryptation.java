package supportive;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


/**
 * Encrypt decrypt
 */
public class Encryptation
{
	public static byte[] encrypt(String pass) throws InvalidKeyException,
											  UnsupportedEncodingException,
											  InvalidKeySpecException,
											  NoSuchAlgorithmException,
											  NoSuchPaddingException,
											  IllegalBlockSizeException,
											  BadPaddingException
	{
		byte[] keyBytes = "1234567890azertyuiopqsdf".getBytes("ASCII");
		DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
		SecretKey key = factory.generateSecret(keySpec);
		byte[] text = pass.getBytes("ASCII");
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(text);
		
		return encrypted;
	}
	
	public static String decrypt(byte[] toDecrypt) throws Exception
	{
		byte[] keyBytes = "1234567890azertyuiopqsdf".getBytes("ASCII");
		DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
		SecretKey key = factory.generateSecret(keySpec);
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(toDecrypt);
		
		return new String(decrypted, "ASCII");
	}
}
