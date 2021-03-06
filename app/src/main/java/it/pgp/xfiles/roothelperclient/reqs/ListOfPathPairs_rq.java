package it.pgp.xfiles.roothelperclient.reqs;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import it.pgp.xfiles.roothelperclient.ControlCodes;
import it.pgp.xfiles.utils.Misc;

/**
 * Created by pgp on 19/07/17
 * One single request for copying/moving/uploading/downloading a selection of files
 */

public class ListOfPathPairs_rq {
    static final Charset UTF8 = Charset.forName("UTF-8");

    public ControlCodes requestType;
    public List<String> v_fx,v_fy; // pathnames

    // Request type to be set by inheritors
    public ListOfPathPairs_rq(List<String> v_fx, List<String> v_fy) {
        this.v_fx = v_fx;
        this.v_fy = v_fy;
    }

    public void write(OutputStream outputStream) throws IOException {
        final byte[] listEnd = new byte[]{0,0,0,0};

        // write control byte
        outputStream.write(requestType.getValue());

        Iterator<String> fxi = v_fx.iterator();
        Iterator<String> fyi = v_fy.iterator();

        while(fxi.hasNext()) {
            byte[] x = fxi.next().getBytes(UTF8);
            byte[] y = fyi.next().getBytes(UTF8);

            // write pair of lengths
            byte[] tmpx,tmpy;
            tmpx = Misc.castUnsignedNumberToBytes(x.length,2);
            tmpy = Misc.castUnsignedNumberToBytes(y.length,2);
            ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()); // change here if length decode error or swapped lengths
            b.put(tmpx);
            b.put(tmpy);
            outputStream.write(b.array());

            // write pair of paths
            outputStream.write(x);
            outputStream.write(y);
        }

        outputStream.write(listEnd);
    }
}
