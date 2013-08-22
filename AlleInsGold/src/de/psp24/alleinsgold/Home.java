package de.psp24.alleinsgold;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.psp24.alleinsgold.data.SchemaHelper;

public class Home extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// create initial database content
		SchemaHelper sh = new SchemaHelper(this);
		long matchId = sh.createMockData();
		
		// remove initial database content
//		sh.removeMatch((int) matchId);
		

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new TabFragment();
			Bundle args = new Bundle();
			args.putInt(TabFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * The main tab fragment representing a section of the app.
	 */
	public static class TabFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public TabFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home_dummy,
					container, false);
			TextView tabTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
				case 1: 
					tabTextView.setText("Hierhin kommt der Inhalt für den Home Tab: Begrüßung des Benutzers, allg Infos, Updates");
					break;
				case 2:
					tabTextView.setText("Hierhin kommt der Inhalt für den Wettkampf Tab: \n- Zeige gespeicherte Wettkämpfe an\n" +
										"- Erstelle einen neuen Wettkampf \n\nEin Wettkampf besteht aus 1-m Durchgängen " +
										"(normalerweise m=2), jeder Durchgang besteht aus 1-n Passen (normalerweise n=10 oder " +
										"n=12), jede Passe besteht aus k Pfeilen (normalerweise k=3, aber manchmal auch k=6)." +
										"m, n, k sowie die Wettkampfdistanz (18m, 25m, 40m oder 70m) und die teilnehmenden Schützen (max 4)" +
										" müssen beim  Anlegen eines neuen Wettkampfes angegeben werden. Das bestimmt dann die nachfolgende" +
										"Treffererfassung. Man sollte dabei jederzeit die Treffererfassung unterbrechen und später wieder " +
										"aufnehmen können.");
					Button button = new Button(getActivity());
					RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					relativeParams.addRule(RelativeLayout.BELOW, R.id.section_label);
					button.setLayoutParams(relativeParams);
					button.setText(R.string.btn_test_shootResultsTable);
					button.setOnClickListener( new View.OnClickListener() {						
						@Override
						public void onClick(View v) {
							Activity activity = getActivity();
							Intent intent = new Intent(activity, ShooterResultsTableActivity.class);
							activity.startActivity(intent);
						}
					});
					((RelativeLayout)rootView).addView(button);
					break;
				case 3:
					tabTextView.setText("Hierhin kommt der Inhalt für den Statistik Tab: verschiedene Auswertungen über alle Wettkämpfe hinweg");
					break;	
			} 
			return rootView;
		}
	}

}
