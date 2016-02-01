package com.daemon.oxfordschool.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.classes.Action;
import com.daemon.oxfordschool.classes.CustomGallery;
import com.daemon.oxfordschool.adapter.GalleryAdapter;
import com.daemon.oxfordschool.listeners.ImagePickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class DialogGallery extends DialogFragment
{
	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;
    AppCompatActivity mActivity;
    FragmentManager mManager;

	TextView text_view_no_media;
	Button btnGalleryOk;

	String action="";
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
    Bundle Args = new Bundle();
    ImagePickListener mCallBack;

    public static String MODULE = "DialogGallery";
    public static String TAG="";

    private Toolbar mToolbar;


	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        TAG = "onCreate";
        Log.d(MODULE, TAG);
        mActivity = (AppCompatActivity)getActivity();
        mManager = mActivity.getSupportFragmentManager();
        Args = getArguments();
        if(Args!=null)
        {
            Log.d(MODULE, TAG + " " + Args);
            action = Args.getString("B_ACTION");
        }
		initImageLoader();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.gallery, container, false);
        TAG = "onCreateView";
        Log.d(MODULE, TAG);
        init(rootView);
        return rootView;
    }

    private void initImageLoader()
    {
        TAG = "initImageLoader";
        Log.d(MODULE, TAG);

		try
        {
            imageLoader = ImageLoader.getInstance();
            options = AppUtils.getOptions();
		}
        catch (Exception e)
        {

		}
	}

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        TAG = "onCreateDialog";
        Log.d(MODULE, TAG);
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void init(View rootView)
    {
        TAG = "init";
        Log.d(MODULE, TAG);

        try
        {
            Dialog dialog = getDialog();
            if (dialog != null)
            {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            Log.d(MODULE, TAG + " " + action);

            handler = new Handler();
            mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            SetToolBar();
            gridGallery = (GridView) rootView.findViewById(R.id.gridGallery);
            gridGallery.setFastScrollEnabled(true);
            adapter = new GalleryAdapter(mActivity, imageLoader);

            if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK))
            {
                Log.d(MODULE, TAG + " " + action);
                rootView.findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
                gridGallery.setOnItemClickListener(mItemMulClickListener);
                adapter.setMultiplePick(true);
            }
            else if (action.equalsIgnoreCase(Action.ACTION_PICK))
            {
                Log.d(MODULE, TAG + " " + action);
                rootView.findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
                gridGallery.setOnItemClickListener(mItemSingleClickListener);
                adapter.setMultiplePick(false);
            }

            gridGallery.setAdapter(adapter);
            text_view_no_media = (TextView) rootView.findViewById(R.id.text_view_no_media);

            btnGalleryOk = (Button) rootView.findViewById(R.id.btnGalleryOk);
            btnGalleryOk.setOnClickListener(mOkClickListener);

            new Thread() {

                @Override
                public void run() {
                    Looper.prepare();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            adapter.addAll(getGalleryPhotos());
                            checkImageStatus();
                        }
                    });
                    Looper.loop();
                };

            }.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.d(MODULE, TAG + " " + ex.getMessage());
        }

	}

    public void SetToolBar()
    {
        TAG = "SetToolBar";
        Log.d(MODULE, TAG);

        try
        {
            mToolbar.setTitle("Select Image");
            mToolbar.setNavigationIcon(mActivity.getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DismissDialog();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

   	private void checkImageStatus()
    {
		if (adapter.isEmpty())
        {
			text_view_no_media.setVisibility(View.VISIBLE);
		}
        else
        {
            text_view_no_media.setVisibility(View.GONE);
		}
	}

    @Override
    public void onStart()
    {
        super.onStart();
        TAG = "onStart";
        Log.d(MODULE, TAG);

    }

    View.OnClickListener mOkClickListener = new View.OnClickListener()
    {
		@Override
		public void onClick(View v)
        {
			ArrayList<CustomGallery> selected = adapter.getSelected();
			String[] allPath = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++)
            {
				allPath[i] = selected.get(i).sdcardPath;
			}
			Intent data = new Intent().putExtra("all_path", allPath);
			//setResult(RESULT_OK, data);
			//finish();
		}
	};

	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id)
        {
			adapter.changeSelection(v, position);
		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id)
        {
            TAG = "mItemSingleClickListener";
            Log.d(MODULE, TAG);
            CustomGallery item = adapter.getItem(position);
            mCallBack.onSingleImagePicked(item.sdcardPath);
            DismissDialog();
			//Intent data = new Intent().putExtra("single_path", item.sdcardPath);
			//setResult(RESULT_OK, data);
			//finish();
		}
	};

	private ArrayList<CustomGallery> getGalleryPhotos()
    {
        TAG = "getGalleryPhotos";
        Log.d(MODULE, TAG);

		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
		try
        {
			final String[] columns = { MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = mActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            Log.d(MODULE, TAG + " imagecursor : " + imagecursor.getCount());
			if (imagecursor != null && imagecursor.getCount() > 0)
            {
				while (imagecursor.moveToNext())
                {
					CustomGallery item = new CustomGallery();
					int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
					item.sdcardPath = imagecursor.getString(dataColumnIndex);
					galleryList.add(item);
				}
			}
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}
		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}

    public void SetImagePickListener(ImagePickListener mCallBack)
    {
        this.mCallBack = mCallBack;
    }

    public void DismissDialog()
    {
        this.dismiss();
        if(mManager!=null) mManager.popBackStack();
    }

}
