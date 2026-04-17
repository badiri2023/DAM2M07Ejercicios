import 'dart:convert';
import 'package:flutter/services.dart';

class DataManager {
  List<dynamic> digimon = [];
  List<dynamic> digivice = [];
  List<dynamic> temporada = [];

  Future<void> loadData() async {
    final rawDigimon = await rootBundle.loadString("assets/data/digimon.json");
    final rawDigivice = await rootBundle.loadString("assets/data/digivice.json");
    final rawTemporada = await rootBundle.loadString("assets/data/temporada.json");

    digimon = jsonDecode(rawDigimon)["digimon"];
    digivice = jsonDecode(rawDigivice)["digivice"];
    temporada = jsonDecode(rawTemporada)["temporada"];
  }
}
