import 'package:flutter/material.dart';
import '../../data/data_manager.dart';

class ResponsiveView extends StatefulWidget {
  const ResponsiveView({super.key});

@override
  State<ResponsiveView> createState() => _ResponsiveViewState();
}

enum MobileViewState { categories, list }

class _ResponsiveViewState extends State<ResponsiveView> {
  final DataManager dataManager = DataManager();

  String selectedCategory = "";
  List<dynamic> items = [];
  Map<String, dynamic>? selectedItem;
  MobileViewState mobileState = MobileViewState.categories;

  @override
  void initState() {
    super.initState();
    _loadAll();
  }

  Future<void> _loadAll() async {
    await dataManager.loadData();
  }

  @override
  Widget build(BuildContext context) {
    final isDesktop = MediaQuery.of(context).size.width > 600;
    return Scaffold(
      appBar: isDesktop ? null : _buildMobileAppBar(),
      body: isDesktop ? _buildDesktop() : _buildMobileBody(),
    );
  }

  // ---------------- PC LAYOUT ----------------
  Widget _buildDesktop() {
    return Column(
      children: [
        _buildHeader(),
        Expanded(
          child: Row(
            children: [
              _buildLeftPanel(),
              Expanded(child: _buildDetailPanel()),
            ],
          ),
        ),
      ],
    );
  }

  // ---------------- MÓVIL LAYOUT ----------------
  PreferredSizeWidget _buildMobileAppBar() {
    return AppBar(
    backgroundColor: const Color(0xFF6A00B8),
    foregroundColor: Colors.white,
    title: Text(
    mobileState == MobileViewState.categories
        ? "Digimon DB"
        : selectedCategory,
        
    ),
      leading: mobileState != MobileViewState.categories
          ? BackButton(
            color: Colors.white,
            onPressed: () {
              setState(() {
                mobileState = MobileViewState.categories;
                selectedCategory = "";
                items = [];
                selectedItem = null;
              });
            },
          )
        : null,
        bottom: const PreferredSize( 
          preferredSize: Size.fromHeight(4), 
        child: ColoredBox( color: Color(0xFF4B0082), 
        child: SizedBox(height: 4), 
        ), 
      ), 
    );
  }

  Widget _buildMobileBody() {
    switch (mobileState) {
      case MobileViewState.categories:
        return ListView(
          children: [
            ListTile(
              title: const Text("Digimon"),
              onTap: () => _selectCategory("Digimon"),
            ),
            ListTile(
              title: const Text("Digivice"),
              onTap: () => _selectCategory("Digivice"),
            ),
            ListTile(
              title: const Text("Temporada"),
              onTap: () => _selectCategory("Temporada"),
            ),
          ],
        );

      case MobileViewState.list:
        return _buildItemList();
    }
  }

  // ---------------- COMPONENTES ----------------

  Widget _buildHeader() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      color: Theme.of(context).colorScheme.primary,
      child: const Text(
        "Digimon DB",
        style: TextStyle(
          fontSize: 24,
          fontWeight: FontWeight.bold,
          color: Colors.white,
        ),
      ),
    );
  }

  Widget _buildLeftPanel() {
    return Container(
      width: 250,
      padding: const EdgeInsets.all(10),
      color: Colors.grey.shade200,
      child: Column(
        children: [
          _buildCategorySelector(),
          const SizedBox(height: 10),
          Expanded(child: _buildItemList()),
        ],
      ),
    );
  }

  Widget _buildCategorySelector() {
    return DropdownButton<String>(
      value: selectedCategory.isEmpty ? "Digimon" : selectedCategory,
      isExpanded: true,
      items: const [
        DropdownMenuItem(value: "Digimon", child: Text("Digimon")),
        DropdownMenuItem(value: "Digivice", child: Text("Digivice")),
        DropdownMenuItem(value: "Temporada", child: Text("Temporada")),
      ],
      onChanged: (value) {
        if (value != null) _selectCategory(value);
      },
    );
  }

  void _selectCategory(String category) {
    setState(() {
      selectedCategory = category;
      selectedItem = null;
      if (category == "Digimon") items = dataManager.digimon;
      if (category == "Digivice") items = dataManager.digivice;
      if (category == "Temporada") items = dataManager.temporada;
      mobileState = MobileViewState.list;
    });
  }

  Widget _buildItemList() {
    return ListView.builder(
      itemCount: items.length,
      itemBuilder: (context, index) {
        final item = items[index];
        final selected = selectedItem == item;

        return GestureDetector(
          onTap: () {
            if (MediaQuery.of(context).size.width <= 600) {
              // Navegación en móvil
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => DetailScreen(
                    item: item,
                    category: selectedCategory,
                  ),
                ),
              );
            } else {
              // Desktop: mostrar en panel derecho
              setState(() {
                selectedItem = item;
              });
            }
          },
          child: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              border: Border(
                bottom: BorderSide(
                  color: selected
                      ? Theme.of(context).colorScheme.secondary
                      : Theme.of(context).colorScheme.primary,
                  width: selected ? 3 : 2,
                ),
              ),
              color: selected
                  ? Theme.of(context).colorScheme.secondary.withOpacity(0.06)
                  : Colors.transparent,
            ),
            child: Row(
              children: [
                if (item["image"] != null)
                  Image.asset(item["image"],
                      height: 40, width: 40, fit: BoxFit.contain),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    item["name"],
                    style: TextStyle(
                      fontWeight:
                          selected ? FontWeight.bold : FontWeight.normal,
                    ),
                    overflow: TextOverflow.ellipsis,
                    maxLines: 1,
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildDetailPanel() {
    if (selectedItem == null) {
      return const Center(child: Text("Selecciona un elemento"));
    }
    return DetailContent(item: selectedItem!, category: selectedCategory);
  }
}

// ---------------- DETALLE COMO PANTALLA SEPARADA ----------------

class DetailScreen extends StatelessWidget {
  final Map<String, dynamic> item;
  final String category;

  const DetailScreen({super.key, required this.item, required this.category});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(item["name"])),
      body: DetailContent(item: item, category: category),
    );
  }
}

class DetailContent extends StatelessWidget {
  final Map<String, dynamic> item;
  final String category;

  const DetailContent({super.key, required this.item, required this.category});

  @override
  Widget build(BuildContext context) {
    switch (category) {
      case "Digimon":
        return _buildDigimonDetail(item);
      case "Digivice":
        return _buildDigiviceDetail(item);
      case "Temporada":
        return _buildSeasonDetail(item);
      default:
        return const SizedBox();
    }
  }

Widget _buildDigimonDetail(Map<String, dynamic> item) {
  return SingleChildScrollView(
    padding: const EdgeInsets.all(20),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        // --- BLOQUE Presentacion ---
        Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Image.asset(item["image"], height: 150, fit: BoxFit.contain),
            const SizedBox(height: 10),
            Text(
              item["name"],
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            Text(
              "Nivel: ${item["level"]}",
              textAlign: TextAlign.center,
            ),
          ],
        ),

        const SizedBox(height: 20),

        // --- BLOQUE Detalles ---
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              item["desc"],
              textAlign: TextAlign.left,
            ),
            const SizedBox(height: 10),

            if (item["evolves_from"] != null)
              Text("Evoluciona desde: ${item["evolves_from"]}"),

            if ((item["evolves_to"] as List).isNotEmpty)
              Text("Evoluciona a: ${(item["evolves_to"] as List).join(", ")}"),
          ],
        ),
      ],
    ),
  );
}


Widget _buildDigiviceDetail(Map<String, dynamic> item) {
  return SingleChildScrollView(
    padding: const EdgeInsets.all(20),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        // --- BLOQUE presentacion ---
        Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            if (item["image"] != null)
              Image.asset(item["image"], height: 150, fit: BoxFit.contain),
            const SizedBox(height: 10),
            Text(
              item["name"],
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            Text(
              "Temporada: ${item["season"]}",
              textAlign: TextAlign.center,
            ),
          ],
        ),

        const SizedBox(height: 20),

        // --- BLOQUE detalles ---
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(item["desc"], textAlign: TextAlign.left),
            const SizedBox(height: 10),

            Text("Funciones: ${(item["functions"] as List).join(", ")}"),

            if ((item["base_digimon"] as List).isNotEmpty)
              Text("Digimon base: ${(item["base_digimon"] as List).join(", ")}"),
          ],
        ),
      ],
    ),
  );
}


Widget _buildSeasonDetail(Map<String, dynamic> item) {
  return SingleChildScrollView(
    padding: const EdgeInsets.all(20),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.center, // bloque principal
      children: [
        // --- BLOQUE SUPERIOR CENTRADO ---
        Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            if (item["image"] != null)
              Image.asset(item["image"], height: 150, fit: BoxFit.contain),
            const SizedBox(height: 10),
            Text(
              item["name"],
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            Text(
              "Año: ${item["year"]}",
              textAlign: TextAlign.center,
            ),
          ],
        ),

        const SizedBox(height: 20),

        // --- BLOQUE INFERIOR IZQUIERDA ---
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(item["desc"], textAlign: TextAlign.left),
            const SizedBox(height: 10),

            ...((item["tamers"] as List).map(
              (tamer) => Padding(
                padding: const EdgeInsets.only(bottom: 4),
                child: Text(
                  "${tamer["name"]} - Partner: ${tamer["partner"] ?? "N/A"} - Digivice: ${tamer["digivice"]}",
                  textAlign: TextAlign.left,
                ),
              ),
            )),
          ],
        ),
      ],
    ),
  );
}

}