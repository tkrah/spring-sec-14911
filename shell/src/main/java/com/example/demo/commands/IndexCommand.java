package com.example.demo.commands;

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import com.example.demo.solr.SolrService;

@Component
public class IndexCommand {

	private final SolrService solrService;

	public IndexCommand(SolrService solrService) {
		this.solrService = solrService;
	}

	@Command(name = "hello", description = "Say hello to a given name", group = "Greetings",
		help = "A command that greets the user with 'Hello ${name}!'. Usage: hello [-n | --name]=<name>")
	public void doNothing() {

	}
}
