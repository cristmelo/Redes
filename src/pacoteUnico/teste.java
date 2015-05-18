package pacoteUnico;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.stream.FileImageInputStream;

public class teste {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		byte[] h =new byte [2];
		ByteBuffer b = ByteBuffer.wrap(h);
		b.order(ByteOrder.BIG_ENDIAN);
		
		System.out.println(h[0]);
	}

}
