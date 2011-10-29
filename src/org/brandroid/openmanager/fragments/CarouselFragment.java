package org.brandroid.openmanager.fragments;

import java.lang.ref.SoftReference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.brandroid.carousel.CarouselViewHelper;
import org.brandroid.carousel.CarouselView;
import org.brandroid.openmanager.IntentManager;
import org.brandroid.openmanager.OpenExplorer;
import org.brandroid.openmanager.R;
import org.brandroid.openmanager.data.OpenFile;
import org.brandroid.openmanager.data.OpenPath;
import org.brandroid.utils.Logger;

public class CarouselFragment extends Fragment {
	private static final String TAG = "CarouselTestActivity";
    private static final int CARD_SLOTS = 56;
    private static final int SLOTS_VISIBLE = 7;

    protected static final boolean DBG = false;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int TEXTURE_WIDTH = 256;
    private static final int DETAIL_TEXTURE_WIDTH = 200;
    private static final int DETAIL_TEXTURE_HEIGHT = 80;
    private static final int VISIBLE_DETAIL_COUNT = 3;
    private static boolean INCREMENTAL_ADD = false; // To debug incrementally adding cards
    private CarouselView mView;
    private Paint mPaint = new Paint();
    private Paint mBlackPaint = new Paint();
    
    private CarouselViewHelper mHelper;
    private Bitmap mGlossyOverlay;
    private Bitmap mBorder;
    
    private OpenPath[] mPathItems;
    
    public int size() { return mPathItems.length; }
    
	class LocalCarouselViewHelper extends CarouselViewHelper {
        private static final int PIXEL_BORDER = 3;
        private DetailTextureParameters mDetailTextureParameters
                = new DetailTextureParameters(5.0f, 5.0f, 3.0f, 10.0f);

        LocalCarouselViewHelper(Context context) {
            super(context);
        }

        @Override
        public void onCardSelected(final int id) {
        	Intent intent = IntentManager.getIntent(mPathItems[id], (OpenExplorer)getActivity(), ((OpenExplorer)getActivity()).getEventHandler());
        	intent.putExtra("path", mPathItems[id].getPath());
        	startActivity(intent);
            //postMessage("Selection", "Card " + id + " was selected");
        }

        @Override
        public void onDetailSelected(final int id, int x, int y) {
        	onCardSelected(id);
        }

        @Override
        public void onCardLongPress(int n, int touchPosition[], Rect detailCoordinates) {
            onCardSelected(n);
        }

        @Override
        public DetailTextureParameters getDetailTextureParameters(int id) {
            return mDetailTextureParameters;
        }

        @Override
        public Bitmap getTexture(int n) {
            Bitmap bitmap = Bitmap.createBitmap(TEXTURE_WIDTH, TEXTURE_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(0, 0, 0, 0);
            mPaint.setColor(0x40808080);
            canvas.drawRect(2, 2, TEXTURE_WIDTH-2, TEXTURE_HEIGHT-2, mPaint);
            mPaint.setColor(0xffffffff);
	        
            SoftReference<Bitmap> thumb = null;
            final OpenPath mPath = mPathItems[n];
            if(mPathItems != null && (thumb = mPath.getThumbnail(TEXTURE_WIDTH, TEXTURE_HEIGHT)) != null && thumb.get() != null)
            {
	        	Bitmap b = thumb.get();
	        	int w = b.getWidth();
	        	int h = b.getHeight();
	        	Logger.LogDebug("Bitmap returned is " + w + "x" + h);
            	Matrix matrix = canvas.getMatrix();
            	matrix.setRectToRect(new RectF(0, 0, w, h), new RectF(3, 3, TEXTURE_WIDTH - 3, TEXTURE_HEIGHT - 3), ScaleToFit.CENTER);
            	canvas.drawRect(new RectF(3, 3, TEXTURE_WIDTH - 3, TEXTURE_HEIGHT - 3), mBlackPaint);
            	//matrix.postTranslate(3, 3);
            	canvas.drawBitmap(b, matrix, null);
            } else {
	            mPaint.setTextSize(100.0f);
	            mPaint.setAntiAlias(true);
	            if(mPath == null)
	            	canvas.drawText("" + n, 2, TEXTURE_HEIGHT-10, mPaint);
	            else
	            {
	            	mPaint.setTextSize(30f);
	            	canvas.drawText(mPath.getName(), 2, TEXTURE_HEIGHT - 10, mPaint);
	            }
            }
            canvas.drawBitmap(mGlossyOverlay, null,
                    new Rect(PIXEL_BORDER, PIXEL_BORDER,
                            TEXTURE_WIDTH - PIXEL_BORDER, TEXTURE_HEIGHT - PIXEL_BORDER), mPaint);
            return bitmap;
        }

        @Override
        public Bitmap getDetailTexture(int n) {
            Bitmap bitmap = Bitmap.createBitmap(DETAIL_TEXTURE_WIDTH, DETAIL_TEXTURE_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(32, 10, 10, 10);
            mPaint.setTextSize(15.0f);
            mPaint.setAntiAlias(true);
            OpenPath mPath = mPathItems[n];
            if(mPath == null)
            	canvas.drawText("Detail text for card " + n, 0, DETAIL_TEXTURE_HEIGHT/2, mPaint);
            else
            	canvas.drawText(mPath.getName(), 0, DETAIL_TEXTURE_HEIGHT / 2, mPaint);
            return bitmap;
        }
    };

    private Runnable mAddCardRunnable = new Runnable() {
        public void run() {
            if (mView.getCardCount() < size()) {
                mView.createCards(mView.getCardCount() + 1);
                mView.postDelayed(mAddCardRunnable, 2000);
            }
        }
    };
    
    private void runOnUiThread(Runnable r) { getActivity().runOnUiThread(r); }

    void postMessage(final CharSequence title, final CharSequence msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
            }
        });
    }
    
    public CarouselFragment()
    {
    	super();
    	mPathItems = new OpenFile[0];
    }
    public CarouselFragment(OpenPath mParent)
    {
    	super();
    	mBlackPaint.setColor(Color.BLACK);
    	mPathItems = mParent.list();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View mParent = inflater.inflate(R.layout.carousel_test, null);
    	
    	mView = (CarouselView)mParent.findViewById(R.id.carousel);
        mHelper = new LocalCarouselViewHelper(getActivity());
        
    	return mParent;
    }
    
    @Override
    public void onViewCreated(View mParent, Bundle savedInstanceState) {
    	super.onViewCreated(mParent, savedInstanceState);
    	mView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mPaint.setColor(0xffffffff);
        
        final Resources res = getResources();
        mHelper.setCarouselView(mView);
        mView.setSlotCount(CARD_SLOTS);
        mView.createCards(INCREMENTAL_ADD ? 1 : size());
        mView.setVisibleSlots(SLOTS_VISIBLE);
        mView.setStartAngle((float) -(2.0f*Math.PI * 5 / CARD_SLOTS));
        mBorder = BitmapFactory.decodeResource(res, R.drawable.border);
        mView.setDefaultBitmap(mBorder);
        mView.setLoadingBitmap(mBorder);
        mView.setBackgroundColor(0.25f, 0.25f, 0.5f, 0.5f);
        mView.setRezInCardCount(3.0f);
        mView.setFadeInDuration(250);
        mView.setVisibleDetails(VISIBLE_DETAIL_COUNT);
        mView.setDragModel(CarouselView.DRAG_MODEL_PLANE);
        if (INCREMENTAL_ADD) {
            mView.postDelayed(mAddCardRunnable, 2000);
        }

        mGlossyOverlay = BitmapFactory.decodeResource(res, R.drawable.glossy_overlay);
        
    }

    @Override
	public void onResume() {
        super.onResume();
        mHelper.onResume();
    }

    @Override
	public void onPause() {
        super.onPause();
        mHelper.onPause();
    }

}