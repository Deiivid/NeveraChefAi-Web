package es.neverachefai.neverachefai

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import neverachefai.shared.generated.resources.Res
import neverachefai.shared.generated.resources.app_icon_neverachef_real
import neverachefai.shared.generated.resources.badge_app_store
import neverachefai.shared.generated.resources.badge_google_play
import neverachefai.shared.generated.resources.feature_fridge
import neverachefai.shared.generated.resources.feature_privacy
import neverachefai.shared.generated.resources.feature_recipes
import neverachefai.shared.generated.resources.feature_shopping
import neverachefai.shared.generated.resources.ic_cat_eggs
import neverachefai.shared.generated.resources.ic_fruit_tomato
import neverachefai.shared.generated.resources.ic_meat_chicken
import neverachefai.shared.generated.resources.ic_vegetable_spinach
import neverachefai.shared.generated.resources.hero_fridge_background
import neverachefai.shared.generated.resources.recipe_arroz_pollo
import neverachefai.shared.generated.resources.recipe_ensalada_tomate_mozzarella
import neverachefai.shared.generated.resources.recipe_tortilla_francesa
import neverachefai.shared.generated.resources.ref_food_eggs
import neverachefai.shared.generated.resources.ref_food_rice
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val BrandGreen = Color(0xFF07883A)
private val BrandGreenDark = Color(0xFF055F31)
private val BrandRed = Color(0xFFE52420)
private val Charcoal = Color(0xFF12201B)
private val MutedText = Color(0xFF5C6862)
private val WarmSurface = Color(0xFFFFFCF7)
private val SoftGreen = Color(0xFFEAF6ED)
private val PrivacyPanel = Color(0xFF18221D)
private val BorderColor = Color(0x1A12201B)

private data class PrivacyEntry(
    val title: String,
    val body: String,
    val bullets: List<String> = emptyList(),
)

private data class ValueContent(
    val icon: DrawableResource,
    val title: String,
    val body: String,
)

private enum class SitePage(val label: String) {
    Home("Inicio"),
    Privacy("Privacy"),
}

private enum class PrivacyLanguage(
    val label: String,
) {
    English("English"),
    Spanish("Español"),
}

@Composable
@Preview
fun App() {
    var selectedPage by remember { mutableStateOf(SitePage.Home) }

    MaterialTheme {
        Surface(color = WarmSurface) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Header(
                    selectedPage = selectedPage,
                    onPageSelected = { selectedPage = it },
                )
                when (selectedPage) {
                    SitePage.Home -> HomeScreen()
                    SitePage.Privacy -> PrivacyScreen()
                }
            }
        }
    }
}

@Composable
private fun Header(
    selectedPage: SitePage,
    onPageSelected: (SitePage) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.96f)),
    ) {
        val compact = maxWidth < 760.dp
        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BrandLockup()
                NavigationTabs(
                    selectedPage = selectedPage,
                    onPageSelected = onPageSelected,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BrandLockup()
                NavigationTabs(
                    selectedPage = selectedPage,
                    onPageSelected = onPageSelected,
                )
            }
        }
    }
}

@Composable
private fun BrandLockup() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.app_icon_neverachef_real),
            contentDescription = "NeveraChefAI",
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp)),
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = BrandGreen)) { append("Nevera") }
                withStyle(SpanStyle(color = BrandRed)) { append("ChefAI") }
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp,
        )
    }
}

@Composable
private fun NavigationTabs(
    selectedPage: SitePage,
    onPageSelected: (SitePage) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SitePage.entries.forEach { page ->
            TopTab(
                text = page.label,
                selected = selectedPage == page,
                onClick = { onPageSelected(page) },
            )
        }
    }
}

@Composable
private fun TopTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            color = if (selected) BrandGreen else Charcoal,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(3.dp)
                .background(if (selected) BrandGreen else Color.Transparent, RoundedCornerShape(8.dp)),
        )
    }
}

@Composable
private fun HomeScreen() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.White, Color(0xFFF5F8F3), Color(0xFFEAF1EA)),
                ),
            ),
    ) {
        val compact = maxWidth < 900.dp
        val narrow = maxWidth < 700.dp
        KitchenBackdrop()
        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (narrow) 460.dp else 500.dp)
                    .padding(horizontal = 28.dp, vertical = if (narrow) 42.dp else 36.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HeroCopy(
                    modifier = Modifier.fillMaxWidth(),
                    compact = narrow,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(horizontal = 64.dp, vertical = 42.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(56.dp),
            ) {
                HeroCopy(Modifier.weight(0.95f))
            }
        }
    }
    ValueStrip()
}

@Composable
private fun HeroCopy(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (compact) 16.dp else 20.dp),
    ) {
        Text(
            text = "Cocina con lo\nque ya tienes",
            color = Color(0xFF071813),
            fontSize = if (compact) 46.sp else 58.sp,
            lineHeight = if (compact) 52.sp else 64.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.sp,
        )
        Text(
            text = "Lleva tu lista de la compra encima y compártela",
            color = Charcoal,
            fontSize = if (compact) 19.sp else 22.sp,
            lineHeight = if (compact) 26.sp else 30.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.widthIn(max = 520.dp),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StoreButton(
                badge = Res.drawable.badge_app_store,
                contentDescription = "Download on the App Store",
                url = "https://apps.apple.com/search?term=NeveraChefAI",
            )
            StoreButton(
                badge = Res.drawable.badge_google_play,
                contentDescription = "GET IT ON Google Play",
                url = "https://play.google.com/store/apps/details?id=es.neverachefai",
            )
        }
    }
}

@Composable
private fun StoreButton(
    badge: DrawableResource,
    contentDescription: String,
    url: String,
) {
    Image(
        painter = painterResource(badge),
        contentDescription = contentDescription,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { openExternalUrl(url) }
            .width(156.dp)
            .height(52.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun KitchenBackdrop() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.hero_fridge_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Color.White.copy(alpha = 0.9f),
                        0.36f to Color.White.copy(alpha = 0.68f),
                        0.7f to Color.White.copy(alpha = 0.08f),
                        1f to Color.Transparent,
                    ),
                ),
        )
    }
}

@Composable
private fun IngredientImage(
    resource: DrawableResource,
    size: Dp,
) {
    Image(
        painter = painterResource(resource),
        contentDescription = null,
        modifier = Modifier.size(size),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun AppPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(22.dp, RoundedCornerShape(26.dp), ambientColor = Color.Black.copy(alpha = 0.16f))
            .clip(RoundedCornerShape(26.dp))
            .background(Color.White)
            .border(1.dp, Color.White, RoundedCornerShape(26.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(BrandGreenDark, BrandGreen)))
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tu despensa", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                CircleAction("+")
            }
            Text("12 ingredientes disponibles", color = Color.White.copy(alpha = 0.82f), fontSize = 13.sp)
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PantryChip(Res.drawable.ic_fruit_tomato, "Tomate")
            PantryChip(Res.drawable.ref_food_eggs, "Huevos")
            PantryChip(Res.drawable.ref_food_rice, "Arroz")
            PantryChip(Res.drawable.ic_meat_chicken, "Pollo")
            PantryChip(Res.drawable.ic_vegetable_spinach, "Espinacas")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Recetas para ti", color = Charcoal, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("Ver todas", color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        RecipeCard(Res.drawable.recipe_arroz_pollo, "Arroz con pollo\ny verduras", "30 min", "95% coincidencia")
        RecipeCard(Res.drawable.recipe_tortilla_francesa, "Tortilla de espinacas\ny queso", "20 min", "90% coincidencia")
        RecipeCard(Res.drawable.recipe_ensalada_tomate_mozzarella, "Ensalada de tomate\ny queso fresco", "15 min", "85% coincidencia")
        BottomAppNav()
    }
}

@Composable
private fun CircleAction(label: String) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(Color.White.copy(alpha = 0.94f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = BrandGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PantryChip(
    resource: DrawableResource,
    label: String,
) {
    Column(
        modifier = Modifier
            .width(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F8F5))
            .padding(vertical = 9.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        IngredientImage(resource, 34.dp)
        Text(label, color = Charcoal, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun RecipeCard(
    image: DrawableResource,
    title: String,
    time: String,
    match: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier
                .size(width = 96.dp, height = 72.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(title, color = Charcoal, fontSize = 16.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold)
            Text("$time  ·  Fácil", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(
                text = match,
                color = BrandGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SoftGreen)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun BottomAppNav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        listOf("Inicio", "Recetas", "Despensa", "Lista", "Perfil").forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(if (index == 0) BrandGreen else Color.Transparent, RoundedCornerShape(6.dp))
                        .border(1.dp, if (index == 0) BrandGreen else BorderColor, RoundedCornerShape(6.dp)),
                )
                Spacer(Modifier.height(4.dp))
                Text(label, color = if (index == 0) BrandGreen else MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ValueStrip() {
    val items = listOf(
        ValueContent(Res.drawable.feature_fridge, "Usa lo que tienes", "Escanea tu nevera o añade ingredientes y encuentra recetas al instante."),
        ValueContent(Res.drawable.feature_recipes, "Recetas inteligentes", "Nuestra IA crea recetas deliciosas según tus ingredientes, tiempo y preferencias."),
        ValueContent(Res.drawable.feature_shopping, "Lista de la compra", "Llévala encima, añade faltantes y compártela cuando quieras."),
        ValueContent(Res.drawable.feature_privacy, "Privacidad primero", "Tus datos y tu despensa son tuyos. Nunca compartimos tu información personal."),
    )
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FBF8))
            .padding(horizontal = 48.dp, vertical = 24.dp),
    ) {
        if (maxWidth < 980.dp) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items.forEach { item ->
                    ValueItem(
                        icon = item.icon,
                        title = item.title,
                        body = item.body,
                        modifier = Modifier.widthIn(min = 280.dp, max = 350.dp),
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items.forEach { item ->
                    ValueItem(
                        icon = item.icon,
                        title = item.title,
                        body = item.body,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ValueItem(
    icon: DrawableResource,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .heightIn(min = 118.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BorderColor, RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Fit,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Charcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(body, color = MutedText, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun PrivacyScreen() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(WarmSurface, Color(0xFFF2F7EF))),
            ),
    ) {
        val compact = maxWidth < 760.dp
        PrivacyPanelCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (compact) 20.dp else 36.dp,
                    vertical = if (compact) 24.dp else 34.dp,
                )
                .align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun PrivacyPanelCard(
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F5E33), PrivacyPanel),
                ),
            )
    ) {
        val panelPadding = if (maxWidth < 760.dp) 20.dp else 32.dp
        var selectedLanguage by remember { mutableStateOf(PrivacyLanguage.English) }
        val isEnglish = selectedLanguage == PrivacyLanguage.English
        Column(
            modifier = Modifier.padding(panelPadding),
            verticalArrangement = Arrangement.spacedBy(26.dp),
        ) {
            PrivacyHeader(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it },
            )
            PrivacyLanguagePanel(
                title = if (isEnglish) "Privacy Policy" else "Política de Privacidad",
                updated = if (isEnglish) "Last updated: June 26, 2026" else "Última actualización: 26 de junio de 2026",
                entries = if (isEnglish) englishPrivacyEntries() else spanishPrivacyEntries(),
            )
        }
    }
}

@Composable
private fun PrivacyHeader(
    selectedLanguage: PrivacyLanguage,
    onLanguageSelected: (PrivacyLanguage) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isEnglish = selectedLanguage == PrivacyLanguage.English
        val compact = maxWidth < 820.dp
        val title = if (isEnglish) "NeveraChefAI Privacy" else "Privacidad de NeveraChefAI"
        val subtitle = if (isEnglish) {
            "Responsible: NeveraChefAI · Contact: neverachefai@gmail.com"
        } else {
            "Responsable: NeveraChefAI · Contacto: neverachefai@gmail.com"
        }
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                PrivacyHeaderText(title, subtitle)
                PrivacyLanguageSelector(selectedLanguage, onLanguageSelected)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                PrivacyHeaderText(
                    title = title,
                    subtitle = subtitle,
                    modifier = Modifier.weight(1f),
                )
                PrivacyLanguageSelector(selectedLanguage, onLanguageSelected)
            }
        }
    }
}

@Composable
private fun PrivacyHeaderText(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.app_icon_neverachef_real),
            contentDescription = null,
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(18.dp)),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.sp,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun PrivacyLanguageSelector(
    selectedLanguage: PrivacyLanguage,
    onLanguageSelected: (PrivacyLanguage) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrivacyLanguage.entries.forEach { language ->
            PrivacyLanguageButton(
                language = language,
                selected = selectedLanguage == language,
                onClick = { onLanguageSelected(language) },
            )
        }
    }
}

@Composable
private fun PrivacyLanguageButton(
    language: PrivacyLanguage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LanguageFlag(language)
        Text(
            text = language.label,
            color = if (selected) PrivacyPanel else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun LanguageFlag(language: PrivacyLanguage) {
    Canvas(modifier = Modifier.size(width = 24.dp, height = 16.dp)) {
        when (language) {
            PrivacyLanguage.English -> {
                drawRect(Color(0xFF012169))
                drawLine(
                    color = Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = size.height * 0.34f,
                )
                drawLine(
                    color = Color.White,
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = size.height * 0.34f,
                )
                drawLine(
                    color = Color(0xFFC8102E),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = size.height * 0.16f,
                )
                drawLine(
                    color = Color(0xFFC8102E),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = size.height * 0.16f,
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(size.width * 0.38f, 0f),
                    size = Size(size.width * 0.24f, size.height),
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(0f, size.height * 0.32f),
                    size = Size(size.width, size.height * 0.36f),
                )
                drawRect(
                    color = Color(0xFFC8102E),
                    topLeft = Offset(size.width * 0.44f, 0f),
                    size = Size(size.width * 0.12f, size.height),
                )
                drawRect(
                    color = Color(0xFFC8102E),
                    topLeft = Offset(0f, size.height * 0.4f),
                    size = Size(size.width, size.height * 0.2f),
                )
            }
            PrivacyLanguage.Spanish -> {
                drawRect(Color(0xFFC60B1E), size = Size(size.width, size.height * 0.28f))
                drawRect(
                    color = Color(0xFFFFC400),
                    topLeft = Offset(0f, size.height * 0.28f),
                    size = Size(size.width, size.height * 0.44f),
                )
                drawRect(
                    color = Color(0xFFC60B1E),
                    topLeft = Offset(0f, size.height * 0.72f),
                    size = Size(size.width, size.height * 0.28f),
                )
            }
        }
        drawRect(Color.Black.copy(alpha = 0.22f), style = Stroke(width = 1.dp.toPx()))
    }
}

@Composable
private fun PrivacyLanguagePanel(
    title: String,
    updated: String,
    entries: List<PrivacyEntry>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(22.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.Black)
                Text(updated, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
            }
        }
        entries.forEach { entry ->
            PrivacySection(entry)
        }
    }
}

@Composable
private fun PrivacySection(entry: PrivacyEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(entry.title, color = Color.White, fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        Text(entry.body, color = Color.White.copy(alpha = 0.84f), fontSize = 14.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium)
        entry.bullets.forEach { bullet ->
            Text("• $bullet", color = Color.White.copy(alpha = 0.78f), fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

private fun englishPrivacyEntries() = listOf(
    PrivacyEntry(
        title = "1. Introduction",
        body = "NeveraChefAI respects user privacy. This Privacy Policy explains what information the app uses, how it is stored, and what control users have over their data. NeveraChefAI is designed with a local-first approach: the app's main data remains stored on the user's device.",
    ),
    PrivacyEntry(
        title = "2. Controller",
        body = "Controller: NeveraChefAI. Contact: neverachefai@gmail.com.",
    ),
    PrivacyEntry(
        title = "3. Data Used by the App",
        body = "NeveraChefAI allows users to save information related to:",
        bullets = listOf(
            "pantry and inventory foods;",
            "shopping list products;",
            "saved or generated recipes;",
            "preferences and app settings.",
        ),
    ),
    PrivacyEntry(
        title = "4. Data We Do Not Collect",
        body = "NeveraChefAI does not create user accounts and does not send the app's main local content to its own servers. We do not ask users to provide:",
        bullets = listOf(
            "name, email address, location, contacts, photos, videos, personal files, financial data, or health data;",
            "browsing history or usage data for creating user profiles.",
        ),
    ),
    PrivacyEntry(
        title = "5. Local Storage",
        body = "Data entered by the user is stored locally on the device. NeveraChefAI does not automatically sync the inventory, shopping list, or recipes with external servers. Users can delete this data in the app when available, by clearing app data in the operating system settings, or by uninstalling the app.",
    ),
    PrivacyEntry(
        title = "6. Microphone",
        body = "NeveraChefAI may request microphone access so users can add foods by voice instead of typing. The microphone is used only when the user voluntarily activates this feature. NeveraChefAI does not record conversations, does not listen in the background, and does not use audio for advertising purposes.",
    ),
    PrivacyEntry(
        title = "7. Local Artificial Intelligence",
        body = "NeveraChefAI may use a local artificial intelligence model based on Gemma to generate recipes when no suitable recipe is available in the app's local database. This process runs on the user's own device. Foods, lists, or preferences used for recipe generation are not sent to external servers for this process.",
    ),
    PrivacyEntry(
        title = "8. Recipes and Generated Content",
        body = "Recipes generated or suggested by NeveraChefAI are for informational and culinary purposes. They may contain errors, inaccuracies, or may not fit every personal need. Users should always review ingredients, allergens, quantities, cooking times, and food safety conditions before preparing or consuming any recipe.",
    ),
    PrivacyEntry(
        title = "9. Shopping List Sharing",
        body = "NeveraChefAI may allow users to share shopping lists with other people through the standard sharing options of the operating system. This only happens when the user voluntarily decides to share a list. NeveraChefAI does not keep an external copy of the shared list and does not control how the selected sharing apps process it.",
    ),
    PrivacyEntry(
        title = "10. Third-Party Services",
        body = "The app may use third-party services needed for distribution, technical operation, ads, or platform features, including Google AdMob where applicable. These services may process technical data, device identifiers, or consent signals according to their own policies. If new third-party services are added for analytics, crash reporting, cloud sync, authentication, payments, or external APIs, this policy will be updated.",
    ),
    PrivacyEntry(
        title = "11. Security",
        body = "NeveraChefAI uses storage mechanisms provided by Android and iOS. Because data is stored locally, information security also depends on the user's device protections, such as screen lock, device encryption, and operating system updates.",
    ),
    PrivacyEntry(
        title = "12. Children",
        body = "NeveraChefAI is not specifically directed to children under 13. The app may be used in a family environment, but children should use it under the supervision of a responsible adult.",
    ),
    PrivacyEntry(
        title = "13. User Rights and Control",
        body = "Because the main data is stored locally on the device and is not transmitted to the app controller, users keep direct control over it. Users can:",
        bullets = listOf(
            "view data inside the app;",
            "modify or delete foods, lists, and recipes;",
            "delete app data from system settings;",
            "uninstall the app to remove its associated local information.",
        ),
    ),
    PrivacyEntry(
        title = "14. Changes to This Privacy Policy",
        body = "This Privacy Policy may be updated to reflect technical, legal, or functional changes. When new features involve personal data handling, this policy will be updated accordingly.",
    ),
)

private fun spanishPrivacyEntries() = listOf(
    PrivacyEntry(
        title = "1. Introducción",
        body = "NeveraChefAI respeta la privacidad de sus usuarios. Esta Política de Privacidad explica qué información utiliza la aplicación, cómo se almacena y qué control tiene el usuario sobre sus datos. NeveraChefAI está diseñada con un enfoque local-first: los datos principales de la aplicación permanecen almacenados en el dispositivo del usuario.",
    ),
    PrivacyEntry(
        title = "2. Responsable",
        body = "Responsable: NeveraChefAI. Contacto: neverachefai@gmail.com.",
    ),
    PrivacyEntry(
        title = "3. Datos que utiliza la aplicación",
        body = "NeveraChefAI permite al usuario guardar información relacionada con:",
        bullets = listOf(
            "alimentos del inventario;",
            "productos de la lista de la compra;",
            "recetas guardadas o generadas;",
            "preferencias y configuración de la aplicación.",
        ),
    ),
    PrivacyEntry(
        title = "4. Datos que no recopilamos",
        body = "NeveraChefAI no crea cuentas de usuario y no envía el contenido local principal de la app a servidores propios. No solicitamos al usuario:",
        bullets = listOf(
            "nombre, correo electrónico, ubicación, contactos, fotografías, vídeos, archivos personales, datos financieros o datos de salud;",
            "historial de navegación ni datos de uso para crear perfiles de usuario.",
        ),
    ),
    PrivacyEntry(
        title = "5. Almacenamiento local",
        body = "Los datos introducidos por el usuario se guardan en el almacenamiento local del dispositivo. NeveraChefAI no sincroniza automáticamente el inventario, la lista de la compra ni las recetas con servidores externos. El usuario puede eliminar estos datos dentro de la app cuando exista esa opción, borrando los datos de la app desde los ajustes del sistema operativo o desinstalando la aplicación.",
    ),
    PrivacyEntry(
        title = "6. Uso del micrófono",
        body = "NeveraChefAI puede solicitar acceso al micrófono para permitir que el usuario introduzca alimentos mediante voz en lugar de escribirlos manualmente. El micrófono se utiliza únicamente cuando el usuario activa voluntariamente esta función. NeveraChefAI no graba conversaciones, no escucha en segundo plano y no utiliza el audio con fines publicitarios.",
    ),
    PrivacyEntry(
        title = "7. Inteligencia artificial local",
        body = "NeveraChefAI puede utilizar un modelo de inteligencia artificial local basado en Gemma para generar recetas cuando no exista una receta adecuada en la base local de la aplicación. Este proceso se ejecuta en el propio dispositivo del usuario. Los alimentos, listas o preferencias utilizados para generar una receta no se envían a servidores externos para este proceso.",
    ),
    PrivacyEntry(
        title = "8. Recetas y contenido generado",
        body = "Las recetas generadas o sugeridas por NeveraChefAI tienen finalidad informativa y culinaria. Pueden contener errores, imprecisiones o no adaptarse a todas las necesidades personales. El usuario debe revisar siempre ingredientes, alérgenos, cantidades, tiempos de cocinado y condiciones de seguridad alimentaria antes de preparar o consumir cualquier receta.",
    ),
    PrivacyEntry(
        title = "9. Compartir listas de la compra",
        body = "NeveraChefAI puede permitir al usuario compartir listas de la compra con otras personas mediante las opciones estándar del sistema operativo. Esta acción solo se produce cuando el usuario decide compartir voluntariamente una lista. NeveraChefAI no conserva una copia externa de la lista compartida ni controla el tratamiento que puedan realizar las aplicaciones elegidas por el usuario.",
    ),
    PrivacyEntry(
        title = "10. Servicios de terceros",
        body = "La app puede utilizar servicios de terceros necesarios para distribución, funcionamiento técnico, anuncios o funciones de plataforma, incluyendo Google AdMob cuando corresponda. Estos servicios pueden tratar datos técnicos, identificadores del dispositivo o señales de consentimiento según sus propias políticas. Si se incorporan nuevos servicios de analítica, informes de errores, sincronización en la nube, autenticación, pagos o APIs externas, esta política será actualizada.",
    ),
    PrivacyEntry(
        title = "11. Seguridad",
        body = "NeveraChefAI utiliza los mecanismos de almacenamiento proporcionados por Android e iOS. Dado que los datos se almacenan localmente, la seguridad de la información también depende de las medidas de protección del dispositivo del usuario, como bloqueo de pantalla, cifrado del dispositivo y actualizaciones del sistema operativo.",
    ),
    PrivacyEntry(
        title = "12. Menores de edad",
        body = "NeveraChefAI no está dirigida específicamente a menores de 13 años. La aplicación puede ser utilizada en un entorno familiar, pero los menores deben utilizarla bajo la supervisión de un adulto responsable.",
    ),
    PrivacyEntry(
        title = "13. Derechos y control del usuario",
        body = "Como los datos principales se almacenan localmente en el dispositivo y no se transmiten al responsable de la aplicación, el usuario mantiene el control directo sobre ellos. El usuario puede:",
        bullets = listOf(
            "consultar los datos dentro de la aplicación;",
            "modificar o eliminar alimentos, listas y recetas;",
            "eliminar los datos de la aplicación desde los ajustes del sistema;",
            "desinstalar la aplicación para eliminar su información local asociada.",
        ),
    ),
    PrivacyEntry(
        title = "14. Cambios en esta Política de Privacidad",
        body = "Esta Política de Privacidad podrá actualizarse para reflejar cambios técnicos, legales o funcionales. Cuando se incorporen nuevas funcionalidades que impliquen tratamiento de datos personales, esta política será actualizada de forma correspondiente.",
    ),
)
