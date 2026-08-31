package com.example.ui.landlord

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.neon.DocumentType
import com.example.data.neon.LandlordUploadedDocument
import com.example.data.neon.NeonRepository
import com.example.data.neon.PropertyGalleryItem
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green100
import com.example.ui.theme.Green50
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red100
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

/**
 * Landlord NRC & Proof of Ownership Document Upload Card
 * Supports Camera photo capture and Gallery image selection
 */
@Composable
fun DocumentUploadCard(
    docType: DocumentType,
    document: LandlordUploadedDocument?,
    onUploadDocument: (DocumentType, String, String, String?) -> Unit,
    onDeleteDocument: (String) -> Unit
) {
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var previewDocument by remember { mutableStateOf<LandlordUploadedDocument?>(null) }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            uploadProgress = 0.3f
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "${docType.name.lowercase()}.jpg"
            val displaySize = "2.4 MB"

            // Simulate quick encryption and Neon upload pipeline
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                uploadProgress = 1.0f
                isUploading = false
                onUploadDocument(docType, fileName, displaySize, uri.toString())
            }, 800)
        }
    }

    // Camera photo launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isUploading = true
            uploadProgress = 0.4f
            val fileName = "Camera_${docType.name.lowercase()}_${System.currentTimeMillis() % 1000}.jpg"
            val displaySize = "1.9 MB"

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                uploadProgress = 1.0f
                isUploading = false
                onUploadDocument(docType, fileName, displaySize, null)
            }, 800)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("doc_upload_card_${docType.name}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (document != null) Green100 else Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (document != null) Green50 else Blue50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (document != null) Icons.Default.CheckCircle else Icons.Default.Description,
                            contentDescription = null,
                            tint = if (document != null) Green600 else Blue600,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = docType.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Text(
                            text = if (docType == DocumentType.NRC_FRONT || docType == DocumentType.NRC_BACK)
                                "Zambian National Registration Card"
                            else "Valid Ministry of Lands Deed / City Council Rates",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                        )
                    }
                }

                if (document != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Green100)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Uploaded ✅",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Green600,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            if (isUploading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Encrypting & uploading to Neon...",
                            style = MaterialTheme.typography.labelSmall.copy(color = Blue600)
                        )
                        Text(
                            text = "${(uploadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(color = Blue600, fontWeight = FontWeight.Bold)
                        )
                    }
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Blue600,
                        trackColor = Slate200,
                    )
                }
            } else if (document != null) {
                // Existing Document Row
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate50,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Encrypted",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = document.fileName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                )
                                Text(
                                    text = "${document.fileSizeFormatted} · Uploaded on ${document.uploadDate}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { previewDocument = document },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Preview",
                                    tint = Blue600,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDeleteDocument(document.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Red600,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Upload trigger button
                Button(
                    onClick = { showSourceDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_btn_${docType.name}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select or Take Document Photo")
                }
            }
        }
    }

    // Source Selection Dialog (Camera vs Gallery)
    if (showSourceDialog) {
        Dialog(onDismissRequest = { showSourceDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Upload ${docType.label}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = "Take a clear photo of your Zambian NRC or document, or choose a file from your device.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showSourceDialog = false
                                cameraLauncher.launch(null)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("choose_camera_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera")
                        }

                        Button(
                            onClick = {
                                showSourceDialog = false
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("choose_gallery_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery")
                        }
                    }
                }
            }
        }
    }

    // Document Preview Dialog
    if (previewDocument != null) {
        Dialog(onDismissRequest = { previewDocument = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = previewDocument!!.fileName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { previewDocument = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    // Mock Visual Document Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Navy900)
                            .border(1.dp, Slate300, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Green600,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "ENCRYPTED KYC DOCUMENT PREVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Verified by BedSpaceZM Trust & Safety",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { previewDocument = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Property Gallery Photos Manager
 * Supports multi-image picker from device gallery and camera captures
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyGalleryManager(
    galleryItems: List<PropertyGalleryItem>,
    onAddImage: (String, String) -> Unit,
    onRemoveImage: (String) -> Unit,
    onSetCover: (String) -> Unit
) {
    var isUploading by remember { mutableStateOf(false) }

    // Multi-image picker launcher
    val multiImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            isUploading = true
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                uris.forEachIndexed { idx, uri ->
                    onAddImage(uri.toString(), "Room Photo ${galleryItems.size + idx + 1}")
                }
                isUploading = false
            }, 600)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isUploading = true
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                onAddImage("camera_pic_${System.currentTimeMillis() % 1000}", "Live Photo ${galleryItems.size + 1}")
                isUploading = false
            }, 500)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("property_gallery_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Property Gallery Photos",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = "${galleryItems.size} photos uploaded (Cover photo is shown in student searches)",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Slate100)
                            .testTag("gallery_camera_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", tint = Navy800, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { multiImageLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Blue50)
                            .testTag("gallery_picker_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Add Images", tint = Blue600, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (isUploading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Blue600)
                    Text("Uploading property photos to Neon backend...", style = MaterialTheme.typography.bodySmall.copy(color = Blue600))
                }
            }

            // Gallery Grid
            if (galleryItems.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate50,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                        .clickable { multiImageLauncher.launch("image/*") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "No property photos yet",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate700)
                        )
                        Text(
                            text = "Tap to upload photos of bedroom, bathroom, study area, and gate",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                    }
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    galleryItems.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate100,
                            modifier = Modifier
                                .width(140.dp)
                                .border(
                                    width = if (item.isCover) 2.dp else 1.dp,
                                    color = if (item.isCover) Blue600 else Slate300,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Navy800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = if (item.isCover) Blue100 else Slate400,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    if (item.isCover) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(4.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Blue600)
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = White, modifier = Modifier.size(10.dp))
                                                Text(text = "Cover", style = MaterialTheme.typography.labelSmall.copy(color = White, fontSize = 9.sp))
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onRemoveImage(item.id) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate700, fontSize = 10.sp),
                                        maxLines = 1
                                    )

                                    if (!item.isCover) {
                                        Text(
                                            text = "Set Cover",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Blue600,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.clickable { onSetCover(item.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
