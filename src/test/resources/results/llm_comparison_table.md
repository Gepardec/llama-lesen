# LLM Evaluation Comparison - Vorfallserhebungsbogen OCR (Weighted)

## 🎯 Overall Performance

| Metric | LLM1 (Llama4) | LLM2 (Qwen3) | LLM3 (Gemini) | Winner |
|--------|---------------|--------------|---------------|--------|
| **Overall Accuracy** | 85.36% | 91.43% | 85.00% | Qwen3 |
| **⭐ WEIGHTED Accuracy** | **49.38%** | **68.96%** 🏆 | **57.29%** | **Qwen3** |
| **Gap to Unweighted** | -36.0% | -22.5% | -27.7% | Qwen3 stabilster |
| **Perfect Forms** | 0/8 (0%) | 0/8 (0%) | 0/8 (0%) | - |

> **Wichtig:** Die gewichtete Accuracy zeigt die Performance bei den **kritischen Fragen** (1, 4, 7 mit Gewicht 10)

## 📊 Detailed Metrics

| Category | LLM1 (Llama4) | LLM2 (Qwen3) | LLM3 (Gemini) | Best |
|----------|---------------|--------------|---------------|------|
| **Checkbox Accuracy** | 96.09% (123/128) | **96.88%** (124/128) | 89.84% (115/128) | Qwen3 ⭐ |
| **Ja/Nein Accuracy** | **92.50%** (37/40) | 87.50% (35/40) | 90.00% (36/40) | Llama4 ⭐ |
| **Text Accuracy** | 70.54% (79/112) | **86.61%** (97/112) | 77.68% (87/112) | Qwen3 ⭐ |
| **Avg Text Similarity** | 65.42% | **86.05%** | 79.73% | Qwen3 ⭐ |

## 🎯 Critical Questions Performance (Weight 10)

| Question | LLM1 (Llama4) | LLM2 (Qwen3) | LLM3 (Gemini) | Best | Impact |
|----------|---------------|--------------|---------------|------|--------|
| **Frage 1** (Datum/Ort) | 12.50% 🔴 | **62.50%** | 25.00% | Qwen3 ⭐ | HOCH |
| **Frage 4** (Verschulden) | 37.50% | 50.00% | **62.50%** | Gemini ⭐ | HOCH |
| **Frage 7** (Alkohol/Drogen) | 50.00% | 62.50% | **75.00%** | Gemini ⭐ | HOCH |
| **Ø Critical (w=10)** | **33.33%** 🔴 | **58.33%** | **54.17%** | **Qwen3** ⭐ |

## 📈 Important Questions Performance (Weight 7)

| Question | LLM1 (Llama4) | LLM2 (Qwen3) | LLM3 (Gemini) | Best |
|----------|---------------|--------------|---------------|------|
| **Frage 3** (Vorfallsort/-art) | **62.50%** | **62.50%** | 37.50% | Llama4/Qwen3 ⭐ |
| **Frage 5** (Ereignis) | 25.00% 🔴 | **100.00%** 🟢 | 50.00% | Qwen3 ⭐ |
| **Ø Important (w=7)** | **43.75%** | **81.25%** 🟢 | **43.75%** | **Qwen3** ⭐ |

## 📋 Standard Questions Performance (Weight 4)

| Question | LLM1 (Llama4) | LLM2 (Qwen3) | LLM3 (Gemini) | Best |
|----------|---------------|--------------|---------------|------|
| **Frage 2** (Frühere Verletzung) | **100.00%** 🟢 | 87.50% | **100.00%** 🟢 | Llama4/Gemini ⭐ |
| **Frage 6** (Raufhandel) | **87.50%** | **87.50%** | 75.00% | Llama4/Qwen3 ⭐ |
| **Frage 8** (Polizei) | **62.50%** | 50.00% | **62.50%** | Llama4/Gemini ⭐ |
| **Frage 9** (Gericht) | **87.50%** | **87.50%** | 62.50% | Llama4/Qwen3 ⭐ |
| **Ø Standard (w=4)** | **84.38%** 🟢 | **78.13%** | **75.00%** | **Llama4** ⭐ |

## 🔍 Checkbox Confusion Matrix

| Metric | LLM1 (Llama4) | LLM2 (Qwen3) | LLM3 (Gemini) | Best |
|--------|---------------|--------------|---------------|------|
| True Positives | **15** | **15** | 10 | Llama4/Qwen3 ⭐ |
| True Negatives | 108 | **109** | 105 | Qwen3 ⭐ |
| False Positives | 3 | **2** | 6 🔴 | Qwen3 ⭐ |
| False Negatives | **2** | **2** | 7 🔴 | Llama4/Qwen3 ⭐ |

## 🏆 Final Rankings

### By Weighted Accuracy (Critical Questions Matter)
1. **🥇 Qwen3: 68.96%** - Klarer Gewinner bei wichtigen Fragen
2. **🥈 Gemini: 57.29%** - Solide bei kritischen Fragen
3. **🥉 Llama4: 49.38%** - Schwach bei Datum/Ort (Frage 1)

### By Unweighted Accuracy (All Questions Equal)
1. **🥇 Qwen3: 91.43%** - Beste Gesamtperformance
2. **🥈 Llama4: 85.36%** - Gut bei unwichtigen Fragen
3. **🥉 Gemini: 85.00%** - Ausgeglichen

## 💡 Key Insights

### 🎯 Critical Findings

1. **Qwen3 dominiert bei wichtigen Fragen**
   - 5× besser als Llama4 bei Frage 1 (62.5% vs 12.5%)
   - Perfekt bei Frage 5 (100%)
   - **19.5 Prozentpunkte Vorsprung** in gewichteter Accuracy

2. **Llama4's fatale Schwäche**
   - Frage 1 (Datum/Ort): nur 12.5% 🔴
   - Frage 5 (Ereignis): nur 25% 🔴
   - Stark bei unwichtigen Fragen (84.4% bei w=4)
   - **→ Ungeeignet für produktiven Einsatz**

3. **Gemini als Kompromiss**
   - Beste Performance bei Frage 7 (Alkohol: 75%)
   - Mittelmäßig überall
   - 7 False Negatives bei Checkboxen (übersieht Markierungen)

### 📉 Consistency Gap

**Gap zwischen gewichteter und ungewichteter Accuracy:**
- Llama4: **-36.0%** → Versagt bei wichtigen Fragen 🔴
- Gemini: **-27.7%** → Probleme mit Prioritäten
- Qwen3: **-22.5%** → Am stabilsten 🟢

### 🚨 Production Recommendation

**Für produktiven Einsatz: Ausschließlich Qwen3**

**Begründung:**
- ✅ 68.96% gewichtete Accuracy (19.5pp besser als Llama4)
- ✅ Beste Text-Erkennung (86.61%)
- ✅ Perfekt bei Ereignisschilderung (100%)
- ✅ Stabilste Performance über alle Fragentypen
- ✅ Niedrigste False Positive/Negative Rate bei Checkboxen

**Llama4 disqualifiziert wegen:**
- ❌ Nur 12.5% bei kritischer Frage 1 (Datum/Ort)
- ❌ Nur 25% bei wichtiger Frage 5 (Ereignis)
- ❌ 49.38% gewichtete Accuracy = unbrauchbar

**Gemini als Backup-Option:**
- ⚠️ Akzeptabel, aber Qwen3 ist klar besser
- ⚠️ Checkbox-Erkennung problematisch (89.84%)