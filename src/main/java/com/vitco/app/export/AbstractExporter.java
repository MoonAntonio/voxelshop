package com.vitco.app.export;

import com.vitco.app.core.data.Data;
import com.vitco.app.core.data.container.Voxel;
import com.vitco.app.layout.content.console.ConsoleInterface;
import com.vitco.app.util.components.progressbar.ProgressDialog;
import com.vitco.app.util.components.progressbar.ProgressReporter;
import com.vitco.app.util.file.FileOut;
import gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.io.IOException;

/**
 * Abstract class for voxel format exporter.
 */
public abstract class AbstractExporter extends ProgressReporter {

    // the file that we export to
    protected final File exportTo;

    // the data that we use
    protected final Data data;

    // wrapper for writing
    protected FileOut fileOut;

    // store voxel information
    private final TIntHashSet colors = new TIntHashSet();
    private final int[] min = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    private final int[] max = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
    private final int[] size = new int[] {0,0,0};
    private final int[] centerSum = new int[] {0, 0, 0};
    private float count = 0;

    // constructor
    public AbstractExporter(File exportTo, Data data, ProgressDialog dialog, ConsoleInterface console) throws IOException {
        super(dialog, console);
        this.exportTo = exportTo;
        this.data = data;

        setActivity("Initializing export...", true);

        // retrieve information
        for (Voxel voxel : data.getVisibleLayerVoxel()) {
            colors.add(voxel.getColor().getRGB());
            min[0] = Math.min(voxel.x, min[0]);
            min[1] = Math.min(voxel.y, min[1]);
            min[2] = Math.min(voxel.z, min[2]);
            max[0] = Math.max(voxel.x, max[0]);
            max[1] = Math.max(voxel.y, max[1]);
            max[2] = Math.max(voxel.z, max[2]);
            // update center sum information
            centerSum[0] += voxel.x;
            centerSum[1] += voxel.y;
            centerSum[2] += voxel.z;
            // update count
            count++;
        }
        size[0] = max[0] - min[0] + 1;
        size[1] = max[1] - min[1] + 1;
        size[2] = max[2] - min[2] + 1;
    }

    // compute and write the file content
    public final boolean writeData() throws IOException {
        fileOut = new FileOut(exportTo.getAbsolutePath());
        boolean wasWritten = false;
        try {
            wasWritten = writeFile();
        } finally {
            fileOut.finish();
        }
        return wasWritten;
    }

    // write the file - to be implemented
    protected abstract boolean writeFile() throws IOException;

    // ===============

    // helper - get all used colors
    protected final int[] getColors() {
        return colors.toArray();
    }

    // helper - get size of voxel batch
    protected final int[] getSize() {
        return size.clone();
    }

    // helper - get minimum voxel position
    protected final int[] getMin() {
        return min.clone();
    }

    // helper - get maximum voxel position
    protected final int[] getMax() {
        return max.clone();
    }

    // helper - get the center of the model
    protected final float[] getCenter() {
        return new float[] {centerSum[0]/count, centerSum[1]/count, centerSum[2]/count};
    }

    // helper - get the amount of voxels
    protected final int getCount() {
        return (int) count;
    }

}
