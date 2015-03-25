/*-------------------------------------------------------------------------
|   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
|   RXTX is a native interface to serial ports in java.
|   Copyright 1997-2009 by Trent Jarvi tjarvi@qbang.org and others who
|   actually wrote it.  See individual source files for more information.
|
|   A copy of the LGPL v 2.1 may be found at
|   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
|   here for your convenience.
|
|   This library is free software; you can redistribute it and/or
|   modify it under the terms of the GNU Lesser General Public
|   License as published by the Free Software Foundation; either
|   version 2.1 of the License, or (at your option) any later version.
|
|   This library is distributed in the hope that it will be useful,
|   but WITHOUT ANY WARRANTY; without even the implied warranty of
|   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
|   Lesser General Public License for more details.
|
|   An executable that contains no derivative of any portion of RXTX, but
|   is designed to work with RXTX by being dynamically linked with it,
|   is considered a "work that uses the Library" subject to the terms and
|   conditions of the GNU Lesser General Public License.
|
|   The following has been added to the RXTX License to remove
|   any confusion about linking to RXTX.   We want to allow in part what
|   section 5, paragraph 2 of the LGPL does not permit in the special
|   case of linking over a controlled interface.  The intent is to add a
|   Java Specification Request or standards body defined interface in the
|   future as another exception but one is not currently available.
|
|   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
|
|   As a special exception, the copyright holders of RXTX give you
|   permission to link RXTX with independent modules that communicate with
|   RXTX solely through the Sun Microsytems CommAPI interface version 2,
|   regardless of the license terms of these independent modules, and to copy
|   and distribute the resulting combined work under terms of your choice,
|   provided that every copy of the combined work is accompanied by a complete
|   copy of the source code of RXTX (the version of RXTX used to produce the
|   combined work), being distributed under the terms of the GNU Lesser General
|   Public License plus this exception.  An independent module is a
|   module which is not derived from or based on RXTX.
|
|   Note that people who make modified versions of RXTX are not obligated
|   to grant this special exception for their modified versions; it is
|   their choice whether to do so.  The GNU Lesser General Public License
|   gives permission to release a modified version without this exception; this
|   exception also makes it possible to release a modified version which
|   carries forward this exception.
|
|   You should have received a copy of the GNU Lesser General Public
|   License along with this library; if not, write to the Free
|   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
|   All trademarks belong to their respective owners.
--------------------------------------------------------------------------*/
package gnu.io;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/**
A class to keep the current version in.
* This is a version for IrScrutinizer. It is not to be used for other purposes.
* It should not be used for rpm-packaging; which should use their own RXTX*.jar
*/

public class RXTXVersion
{
/*------------------------------------------------------------------------------
	RXTXVersion
	accept:       -
	perform:      Set Version.
	return:       -
	exceptions:   Throwable
	comments:
		      See INSTALL for details.
------------------------------------------------------------------------------*/
    private static String Version;
    private static HashMap<String, Boolean> isLoaded = new HashMap<String, Boolean>();

    static {
        RXTXVersion.loadLibrary("rxtxSerial");
        Version = "RXTX-2.2pre2";
    }

    /**
     * static method to return the current version of RXTX unique to RXTX.
     *
     * @return a string representing the version "RXTX-1.4-9"
     */
    public static String getVersion() {
        return (Version);
    }

    public static native String nativeGetVersion();

    static void loadLibrary(String baseName) {
        if (isLoaded.get(baseName) != null)
            return;

        boolean success = systemLoadLibrary(baseName) || localLoadLibrary(baseName);
    }

    // First try the system path
    private static boolean systemLoadLibrary(String baseName) {
        try {
            System.loadLibrary(baseName);
            isLoaded.put(baseName, Boolean.TRUE);
            return true;
        } catch (UnsatisfiedLinkError e1) {
            return false;
        }
    }

    // try the com.hifiremote.LoadLibrary way, i.e. with local,
    // architecture dependent subdirectories...
    private static boolean localLoadLibrary(String baseName) {
        String folderName = (System.getProperty("os.name").startsWith("Windows")
                ? "Windows"
                : System.getProperty("os.name")) + '-' + System.getProperty("os.arch").toLowerCase(Locale.US);
        // supported values: Linux-{i386,amd64}, Mac OS X-{i386,x86_64}, Windows-{x86,amd64}
        // Mac: it appears that Snow Leopard says "X64_64" while Mountain Lion says "x86_64".
        String mappedName = System.mapLibraryName(baseName);
        String libraryFile = folderName + File.separator + mappedName;
        if (loadAbsoluteLibrary(baseName, libraryFile))
            return true;

        // Mac OS X changed the extension of JNI libs recently, give it another try in this case
        if (System.getProperty("os.name").startsWith("Mac")) {
            mappedName = System.mapLibraryName(baseName).replaceFirst("\\.dylib", ".jnilib");
            libraryFile = folderName + File.separator + mappedName;
            return loadAbsoluteLibrary(baseName, libraryFile);
        } else {
            return false;
        }
    }

    private static boolean loadAbsoluteLibrary(String baseName, String path) {
        try {
            System.load((new File(path)).getAbsolutePath());
            isLoaded.put(baseName, Boolean.TRUE);
            return true;
        } catch (UnsatisfiedLinkError e) {
            return false;
        }
    }
}
