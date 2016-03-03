/*
 * Copyright (c) 2016. MergedReadExtractor Alexander Peltzer
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.google.common.io.Files;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by peltzer on 04.02.14.
 */
public class MergedReadExtractor {
    private final SAMFileReader inputSam;
    private final SAMFileWriter outputSam;

    public MergedReadExtractor(File f, String outputpath) {
       inputSam = new SAMFileReader(f);
       inputSam.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
       File output = new File(outputpath + "/"+ Files.getNameWithoutExtension(f.getAbsolutePath()) + "_onlyMerged.bam");
       outputSam = new SAMFileWriterFactory().makeSAMOrBAMWriter(inputSam.getFileHeader(), false, output);

    }


    public static void main(String[] args){
       if(args.length != 2){
           System.out.println("Specify one single SAM/BAM file as input please & an output path to store the file.");
           System.exit(1);
       } else {
           MergedReadExtractor mgre = new MergedReadExtractor(new File(args[0]), args[1]);
           mgre.readSAMFile();
           mgre.outputSam.close();
       }
    }

    /**
     * This Method reads a SAM File and parses the input
     * Currently, only merged Reads with the "M" Flag in front are checked for Duplicates.
     * R/F Flags are simply written into output File, also other "non-flagged" ones.
     *
     * @throws java.io.IOException
     */
    private void readSAMFile() {
        String line = "";
        Iterator it = inputSam.iterator();
        while (it.hasNext()) {
            SAMRecord curr = (SAMRecord) it.next();
            if (curr.getReadName().startsWith("M_")){//&& !curr.getReadName().endsWith("A") && !curr.getReadName().endsWith("B")) {    //Merged Reads only here
                outputSam.addAlignment(curr);
            }
        }
    }



}
