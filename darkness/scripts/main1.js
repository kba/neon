if(journal.hasQuest("travel to Ban Rajas") && !journal.finishedQuest("travel to Ban Rajas")) {
	engine.show('I arrived in Ban Rajas. I should find someone who can tell me more about the mines of Emegir.')
	journal.updateQuest("travel to Ban Rajas", 50, "I arrived in Ban Rajas. I should find someone who can tell me more about the mines of Emegir")
	journal.finishQuest("travel to Ban Rajas")
}