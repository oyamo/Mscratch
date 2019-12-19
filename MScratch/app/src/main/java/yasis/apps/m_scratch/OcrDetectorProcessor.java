/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yasis.apps.m_scratch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import android.widget.Toast;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import yasis.apps.m_scratch.ui.camera.GraphicOverlay;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Context context;
    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay,Context ctx) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.context = ctx;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            String regex = "[0-9]{12,16}";
            boolean matches = item.getValue().replace(" ","").matches(regex);
            boolean confirmedValid = false;
            if(matches){
                Intent intent = new Intent(context,RechargeActivity.class);

                switch (item.getValue().replace(" ","").length()){
                    case 12:
                        String reTelcom = "[0-9]{4} [0-9]{4} [0-9]{4}";
                        if(item.getValue().matches(reTelcom)){
                            intent.putExtra("Carrier","Telcom");
                            confirmedValid = true;
                        }

                        break;
                    case 14:
                        String reAirtel = "\\d{5} \\d{5} \\d{4}";
                       if(item.getValue().matches(reAirtel)){
                           intent.putExtra("Carrier","Airtel");
                           confirmedValid = true;
                       }
                        break;
                    case 16:
                        String reSafcom = "\\d{4} \\d{4} \\d{4} \\d{4}";
                        if(item.getValue().matches(reSafcom)){
                            intent.putExtra("Carrier","Safaricom");
                            confirmedValid = true;
                        }
                }
                if(confirmedValid){
                    intent.putExtra("code",item.getValue());
                    context.startActivity(intent);
                }

            }

            mGraphicOverlay.add(graphic);
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
