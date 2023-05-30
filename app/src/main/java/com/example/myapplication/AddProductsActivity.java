package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddProductsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText idEditText;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceAEditText;
    private EditText priceBEditText;
    private Button selectImageButton;
    private ImageView selectedImageView;
    private Button addProductButton;

    private Uri imageUri;

    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        idEditText = findViewById(R.id.id_edit_text);
        nameEditText = findViewById(R.id.name_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        priceAEditText = findViewById(R.id.price_a_edit_text);
        priceBEditText = findViewById(R.id.price_b_edit_text);
        selectImageButton = findViewById(R.id.select_image_button);
        selectedImageView = findViewById(R.id.selected_image_view);
        addProductButton = findViewById(R.id.add_product_button);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri);
        }
    }

    private void addProduct() {
        String id = idEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priceA = priceAEditText.getText().toString().trim();
        String priceB = priceBEditText.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || description.isEmpty() || priceA.isEmpty() || priceB.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = storageReference.child("product_images").child(id);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri imageUrl) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();

                                    Map<String, Object> productData = new HashMap<>();
                                    productData.put("id", id);
                                    productData.put("name", name);
                                    productData.put("description", description);
                                    productData.put("price", priceA);
                                    productData.put("image", imageUrl.toString());
                                    productData.put("userId", userId);

                                    firestore.collection("productA").document(id)
                                            .set(productData, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    productData.put("price", priceB);

                                                    firestore.collection("productB").document(id)
                                                            .set(productData, SetOptions.merge())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(AddProductsActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                                                    clearFields();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(AddProductsActivity.this, "Failed to add product to productB", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddProductsActivity.this, "Failed to add product to productA", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(AddProductsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProductsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearFields() {
        idEditText.setText("");
        nameEditText.setText("");
        descriptionEditText.setText("");
        priceAEditText.setText("");
        priceBEditText.setText("");
        selectedImageView.setImageURI(null);
        imageUri = null;
    }
}
