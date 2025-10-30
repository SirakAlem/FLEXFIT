package it.unimib.flexfit.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import it.unimib.flexfit.R;
import it.unimib.flexfit.ui.LoginActivity;
public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigation;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuthentication();
        setupToolbar();
        setupNavigation();
        setupBottomNavigation();
    }
    private void checkUserAuthentication() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void setupToolbar() {
    }
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }
    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            try {
                if (itemId == R.id.nav_exercises) {
                    navController.navigate(R.id.categoriesFragment);
                    return true;
                } else if (itemId == R.id.nav_workouts) {
                    navController.navigate(R.id.myWorkoutsFragment);
                    return true;
                } else if (itemId == R.id.nav_favorites) {
                    navController.navigate(R.id.favoritesFragment);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    logout();
                    return true;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }
    private void logout() {
        new AlertDialog.Builder(this, R.style.Theme_FlexFit_AlertDialog)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    firebaseAuth.signOut();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}