import sys
import re

from pygments.lexer import RegexLexer, include, bygroups
from pygments.token import Error, Punctuation, Literal, Token, \
     Text, Comment, Operator, Keyword, Name, String, Number, Generic, \
     Whitespace

__all__ = ['AADLLexer']

class AADLLexer(RegexLexer):
    """
    For `AADL <http://www.aadl.info>`_ source code.
    """

    name = 'AADL'
    aliases = ['aadl']
    filenames = ['*.aadl']
    mimetypes = ['text/x-aadl']

    flags = re.MULTILINE | re.DOTALL

    iden_rex = r'[a-zA-Z_][a-zA-Z0-9_\.]*'
    class_iden_rex = r'(' + iden_rex + r')(::)('+ iden_rex + r')'
    definition_rex = r'(' + iden_rex + r')' +  r'(\s*:\s*)\b'
    keyword_rex = r'(device|system|feature|port|connection|process|thread|data|abstract)'
    emv2_keyword_rex = r'(error)(\s*)(type)'
    
    with_tuple = (r'(with)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'with-list')
    text_tuple = (r'[^\S\n]+', Text)
    terminator_tuple = (r'(;)(\s+)', bygroups(Punctuation, Whitespace), '#pop')
    comment_tuple = (r'--.*?$', Comment.Single)
    comment_whitespace_tuple = (r'(--.*?$)(\s+)', bygroups(Comment.Single, Whitespace))
    
    tokens = {
        'packageOrSystem': [
             text_tuple,
             (r'(implementation)(\s+)(' + iden_rex + r')', bygroups(Name.Class, Whitespace, Name.Class), '#pop'),
             (iden_rex, Name.Class, '#pop'),
         ],
        'annex': [
             text_tuple,
             (r'(\s*)({\*\*)(\s+)', bygroups(Whitespace, Punctuation, Whitespace)),
             (r'(use)(\s+)(types)(\s+)', bygroups(Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace), 'types-list'),
             (r'(use)(\s+)(behavior)(\s+)' + class_iden_rex + '(;)(\s+)', bygroups(Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Name.Class, Punctuation, Name.Entity, Punctuation, Whitespace)),
             (r'(error)(\s+)(propagations)(\s+)', bygroups (Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace), 'error-propagations'),
             (r'(connection)(\s+)(error)(\s+)', bygroups (Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace), 'connection-error'),
             (r'(component)(\s+)(error)(\s+)(behavior)(\s+)', bygroups (Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace), 'component-error-behavior'),
             (r'(properties)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'property-section'),
             (r'(error)(\s+)(behavior)(\s+)(' + iden_rex + ')(\s+)', bygroups (Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Name.Namespace, Whitespace), 'error-behavior'),
             (r'(error)(\s+)(types)(\s+)', bygroups (Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace), 'error-types'),
             (r'(\*\*})(\s*)(;)?(\s+)', bygroups(Punctuation, Whitespace, Punctuation, Whitespace), '#pop'),
             comment_whitespace_tuple,
             (iden_rex, Name.Class),
         ],
        'error-behavior' : [
	         (r'(states)(\s+)', bygroups(Keyword.Namespace, Whitespace)),
        	 (r'(' + iden_rex + ')(\s*)(:)(\s*)(initial)?(\s*)(state)(\s*)(;)(\s*)', bygroups(Name.Attribute, Whitespace, Punctuation, Whitespace, Keyword.Declaration, Whitespace, Keyword.Declaration, Whitespace, Punctuation, Whitespace)),
	         (r'(end)(\s+)(behavior)(\s*)(;)(\s*)', bygroups(Keyword, Whitespace, Keyword.Namespace, Whitespace, Punctuation, Whitespace), '#pop'),        	 
        ],
        'component-error-behavior' : [
        	(r'(events|transitions)(\s+)', bygroups(Keyword.Namespace, Whitespace)),
            (r'(' + iden_rex + ')(\s*)(:)(\s*)(error)(\s+)(event)(\s*)({)(\s*)', bygroups(Name.Variable.Instance, Whitespace, Punctuation, Whitespace, Keyword.Declaration, Whitespace, Keyword.Declaration, Whitespace, Punctuation, Whitespace), 'error-propagation-list'), # Exact same syntax as propagation list, so we reuse it here)
            (r'(' + iden_rex + ')(\s*)(:)(\s*)(' + iden_rex + ')(\s*)(-\[)(\s*)', bygroups(Name.Variable.Instance, Whitespace, Punctuation, Whitespace, Name.Variable.Global, Whitespace, Operator, Whitespace), 'error-transition-list'),
        	(r'(end)(\s+)(component)(\s*)(;)(\s+)', bygroups(Keyword, Whitespace, Keyword.Namespace, Whitespace, Punctuation, Whitespace), '#pop'),
        	comment_whitespace_tuple,
        ],
        'error-transition-list' : [
	        #The use of Name.Attribute here is a hack because SAnToS's stylesheet doesn't differentiate between global (which this should be) and class variables, which makes the code much harder to read.
        	(r'(' + iden_rex + ')(\s+)(ormore|orless|or|and)(\s+)', bygroups(Name.Attribute, Whitespace, Operator, Whitespace)),
        	(r'(' + iden_rex + ')(\s+)', bygroups(Name.Attribute, Whitespace)),
        	(r'(\]->)(\s*)(' + iden_rex + ')(\s*)', bygroups(Operator, Whitespace, Name.Variable.Instance, Whitespace)),
        	terminator_tuple,
        ],
        'types-list' : [
        	(r'(' + iden_rex+ ')(\s*)(,)(\s*)', bygroups(Name.Namespace, Whitespace, Punctuation, Whitespace)),
        	(r'(' + iden_rex+ ')(\s*)(;)(\s+)', bygroups(Name.Namespace, Whitespace, Punctuation, Whitespace), '#pop'),
        ],
        'connection-error' : [
        	(r'(' + iden_rex + ')(\s*)(:)(\s*)(error)(\s+)(source)(\s+)(' + iden_rex + ')(\s*)(;)(\s+)' , bygroups(Name.Variable.Instance, Whitespace, Punctuation, Whitespace, Keyword.Pseudo, Whitespace, Keyword.Pseudo, Whitespace, Name.Variable.Class, Whitespace, Punctuation, Whitespace)),
        	(r'(end)(\s*)(connection)(\s*)(;)(\s+)', bygroups(Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Punctuation, Whitespace), '#pop'),
        ],
        'error-types' : [
        	(r'(' + iden_rex + ')(\s*)(:)(\s*)', bygroups(Name.Attribute, Whitespace, Punctuation, Whitespace), 'error-type-declaration'),#The use of Name.Attribute here is a hack because SAnToS's stylesheet doesn't differentiate between global (which this should be) and class variables, which makes the code much harder to read.
        	(r'(' + iden_rex + ')(\s+)(renames)(\s+)(type)(\s+)' + class_iden_rex, bygroups(Name.Attribute, Whitespace, Keyword.Pseudo, Whitespace, Keyword.Pseudo, Whitespace, Name.Namespace, Punctuation, Name.Variable.Class), 'error-type-declaration'),
        	(r'(end)(\s+)(types)(\s*)(;)(\s+)', bygroups(Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Punctuation, Whitespace), '#pop'),
        	comment_whitespace_tuple,
        ],
        'error-type-declaration' : [
        	(r'(type)(\s+)(extends)(\s+)' + class_iden_rex, bygroups(Keyword.Type, Whitespace, Keyword.Pseudo, Whitespace, Name.Namespace, Punctuation, Name.Variable.Class)),
        	(r'(type)(\s+)(extends)(\s+)(' + iden_rex + ')', bygroups(Keyword.Type, Whitespace, Keyword.Pseudo, Whitespace, Name.Variable.Class)),
        	(r'(type)(\s*)(;)(\s*)', bygroups(Keyword.Type, Whitespace, Punctuation, Whitespace), '#pop'),
        	terminator_tuple,
        ],
        'error-propagations' : [
        	(r'(' + iden_rex + ')(\s*)(:)(\s*)(not)?(\s*)(out|in)(\s+)(propagation)(\s*)({)', bygroups(Name.Variable.Class, Whitespace, Punctuation, Whitespace, Keyword.Type, Whitespace, Keyword.Type, Whitespace, Keyword.Type, Whitespace, Punctuation), 'error-propagation-list'),
        	(r'(flows)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'error-propagation-flows'),
        	(r'(end)(\s+)(propagations)(\s*)(;)(\s+)', bygroups(Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Punctuation, Whitespace), '#pop'),
        	comment_whitespace_tuple,
         ],
         'error-propagation-flows' : [
        	(r'(' + iden_rex + ')(\s*)(:)(\s*)(error)(\s+)(source|sink|path)(\s+)(' + iden_rex + ')(\s*)({)', bygroups(Name.Variable, Whitespace, Punctuation, Whitespace, Keyword.Type, Whitespace, Keyword.Type, Whitespace, Name.Variable.Class, Whitespace, Punctuation), 'error-propagation-list'), # Exact same syntax as propagation list, so we reuse it here
        	(r'(\s*)(->)(\s*)(' + iden_rex + ')(\s*)({)', bygroups(Whitespace, Operator, Whitespace, Name.Variable.Class, Whitespace, Punctuation), 'error-propagation-list'),
        	(r'(end)(\s+)(propagations)(\s*)(;)(\s+)', bygroups(Keyword.Namespace, Whitespace, Keyword.Namespace, Whitespace, Punctuation, Whitespace), '#pop:2'),
        	(r'(when)(\s*)({)', bygroups(Keyword.Declaration, Whitespace, Punctuation), 'error-propagation-list'), 
        	comment_whitespace_tuple,
         ],
        'error-propagation-list' : [
            (r'\s*(,)\s*', Punctuation),
            (r'[a-zA-Z_]\w*', Name.Attribute), #See comment in error-types
            (r'(})(;)?(\s*)', bygroups(Punctuation, Punctuation, Whitespace), '#pop')
        ],
        'with-list' : [
            (r'\s*(,)\s*', Punctuation),
            (r'[a-zA-Z_]\w*', Name.Namespace),
            terminator_tuple, 
        ],
        'package-declaration' : [
            text_tuple,
            (r'(implementation)', Keyword.Declaration),
            (r'(' + iden_rex + r')(;)', bygroups(Name.Class, Punctuation), '#pop'),
            (iden_rex, Name.Class, '#pop'),
        ],
        'declaration' : [
            text_tuple,
            (r'(in|out|event|data)', Keyword.Type),
            (r'(flow|path|port|feature|thread)', Keyword.Type),
            (class_iden_rex, bygroups(Name.Class, Punctuation, Name.Entity)),
            (r'(' + iden_rex + r')(\s+)(->|<-|<->)(\s+)('+ iden_rex + r')', bygroups(Name.Variable, Whitespace, Operator, Whitespace, Name.Variable)),
            (iden_rex, Name.Function),
            (r'(\s*)({)(\s*)', bygroups(Whitespace, Punctuation, Whitespace), 'property-constant-declaration'),
            (r'}', Punctuation),
            terminator_tuple, 
        ],
        'applies-to' : [
            text_tuple,
            (r'\(', Punctuation),
            (r'\s*(,)\s*', Punctuation),
            (r'\s*(\*\*)\s*', Operator),
            (keyword_rex, Keyword.Type),
            (r'(\{)(' + iden_rex + r')(\})', bygroups(Punctuation, Name.Class, Punctuation)),
            (r'\)', Punctuation, '#pop:2'),
        ],
        'property-value' : [
            (r'[0-9]+\.[0-9]*', Number.Float),
            (r'[0-9]+', Number.Integer),
            (r'(reference)(\s*)(\()(' + iden_rex + ')(\))', bygroups(Keyword.Declaration, Whitespace, Punctuation, Name.Variable.Instance, Punctuation)),
            (r'"[^"]*"', Literal.String.Double),
            (r'(\s*)(ps|ns|us|ms|sec|min|hr)(\s*)', bygroups(Whitespace, Literal, Whitespace)),
            (r'(\s*)(\.\.)(\s+)', bygroups(Whitespace, Operator, Whitespace)),
            (r'(\()(\s*)', bygroups(Punctuation, Whitespace), 'property-value-list'),
        	(r'(\[)(\s*)', bygroups(Punctuation, Whitespace), 'property-value-record'),
       	 	(r'(\])(\s*)(\))', bygroups(Punctuation, Whitespace, Punctuation)),
       	 	(r'(\])(\s*)(\,)(\s*)', bygroups(Punctuation, Whitespace, Punctuation, Whitespace)),
        	comment_whitespace_tuple,
            (class_iden_rex, bygroups(Name.Class, Punctuation, Name.Constant)),
            (iden_rex, Name.Constant),
        ],
        'property-value-list' : [
        	include('property-declaration'),
        	include('property-value'),
        	(r'(\s*)(,)(\s*)', bygroups(Whitespace, Punctuation, Whitespace)),
        	(r'(\s*)(\))(\s*)(;)(\s*)', bygroups(Whitespace, Punctuation, Whitespace, Punctuation, Whitespace), '#pop'),
        ],
        'property-value-record' : [
	    	include('property-declaration'),
        	include('property-value'),
            (r'(\s*)(\])(\s*)', bygroups(Whitespace, Punctuation, Whitespace), '#pop'),
        ],
        'applies-to-property-value' : [
            (r'(\s*)(applies to)(\s+)', bygroups(Whitespace, Keyword, Whitespace), 'applies-to'),
            include('property-value'),
            comment_whitespace_tuple,
            (r'(;)(\s+)', bygroups(Punctuation, Whitespace), '#pop:2'),
        ],
        'property-section-property-value' : [
             include('property-value'),
             terminator_tuple,
        ],
        'property-constant-value' : [
            include('property-value'),
            (r'(;)(\s*)', bygroups(Punctuation, Whitespace), '#pop:2')
        ],
        'aggregate-property-constant-list' : [
	    	(r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace)),
            include('property-value'),
            (r'\s*;\s*', Punctuation),
            (r'(\];)(\s+)', bygroups(Punctuation, Whitespace), '#pop:2'),
            (r'(\])(\s+)(applies)(\s+)(to)(\s+)(' + iden_rex +')(;)(\s+)', bygroups(Punctuation, Whitespace, Keyword.Declaration, Whitespace, Keyword.Declaration, Whitespace, Name.Variable.Instance, Punctuation, Whitespace), '#pop'),
        ],
        'property-declaration' : [
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace), 'applies-to-property-value'),
            terminator_tuple,
            comment_whitespace_tuple,
        ],
        'property-constant-declaration' : [
            text_tuple,
            (class_iden_rex + r'(\s*)(=>)(\s*)(\[)(\s*)', bygroups(Name.Class, Punctuation, Name.Constant, Whitespace, Operator, Whitespace, Punctuation, Whitespace), 'aggregate-property-constant-list'),
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)(\[)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace, Punctuation, Whitespace), 'aggregate-property-constant-list'),
            (class_iden_rex + r'(\s*)(=>)(\s*)', bygroups(Name.Class, Punctuation, Name.Constant, Whitespace, Operator, Whitespace), 'property-constant-value'),
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace), 'property-constant-value'),
        ],
        'property-set' : [
            with_tuple,
            (r'(' + iden_rex + r')(\s+)(is)(\s+)', bygroups(Name.Class, Whitespace, Keyword.Namespace, Whitespace)),
            (definition_rex + r'(constant)', bygroups(Name.Variable.Global, Punctuation, Keyword), 'property-constant-declaration'),
            (definition_rex + r'(type)(\s+)(record)(\s+)(\()(\s*)', bygroups(Name.Variable.Global, Punctuation, Keyword.Declaration, Whitespace, Keyword.Declaration, Whitespace, Punctuation, Whitespace), 'property-type-declaration'),
            (definition_rex + r'(record)(\s+)(\()(\s*)', bygroups(Name.Variable.Global, Punctuation, Keyword.Declaration, Whitespace, Punctuation, Whitespace), 'applies-to-property-value-decalration'),
            (definition_rex, bygroups(Name.Variable.Global, Punctuation), 'property-declaration'),
            comment_whitespace_tuple,
            (r'(end)(\s+)(' + iden_rex + r')(;)', bygroups(Keyword.Namespace, Whitespace, Name.Class, Punctuation)),
        	terminator_tuple,
        ],
        'applies-to-property-value-decalration' : [
        	(r'(\s*)(applies to)(\s+)', bygroups(Whitespace, Keyword, Whitespace), 'applies-to'),
        	(r'(\))(\s*)', bygroups(Punctuation, Whitespace)),
        	include('property-type-declaration'),
        ],
        'property-type-declaration' : [
        	(definition_rex, bygroups(Name.Variable.Global, Punctuation)),
        	(r'(aadlstring|aadlinteger|aadlreal)', Keyword.Type),
        	(r'(list)(\s*)(of)(\s*)', bygroups(Keyword.Type, Whitespace, Keyword, Whitespace)),
        	(r'(;)(\s*)', bygroups(Punctuation, Whitespace)),
        	(r'(reference)(\s*)(\()(\s*)' + keyword_rex, bygroups(Keyword.Declaration, Whitespace, Punctuation, Whitespace, Keyword.Type), 'keyword-list'),
        	(r'(reference)(\s*)(\()(\s*)(\{emv2\}\*\*)(\s*)' + emv2_keyword_rex + '(\))(\s*)', bygroups(Keyword.Declaration, Whitespace, Punctuation, Whitespace, Keyword.Namespace, Whitespace, Keyword.Type, Whitespace, Keyword.Type, Punctuation, Whitespace)),
        	(class_iden_rex, bygroups(Name.Namespace, Punctuation, Name.Class)),
            (r'(\))(\s*)(;)(\s*)', bygroups(Punctuation, Whitespace, Punctuation, Whitespace), '#pop'),
        ],
        'keyword-list' : [
        	(r'(,)(\s*)' + keyword_rex, bygroups(Punctuation, Whitespace, Keyword.Type)),
			(r'(\))(\s*)(;)(\s*)', bygroups(Punctuation, Whitespace, Punctuation, Whitespace), '#pop'),
        ],
        'property-section' : [
            text_tuple,
            comment_whitespace_tuple,
            (class_iden_rex + r'(\s*)(=>)(\s*)(\[)(\s*)', bygroups(Name.Class, Punctuation, Name.Constant, Whitespace, Operator, Whitespace, Punctuation, Whitespace), 'aggregate-property-constant-list'),
            (class_iden_rex + r'(\s*)(=>)(\s*)', bygroups(Name.Class, Punctuation, Name.Class, Whitespace, Operator, Whitespace), 'property-section-property-value'),
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace), 'property-section-property-value'),
            (r'(\*\*})(\s*)(;)(\s*)', bygroups(Punctuation, Whitespace, Punctuation, Whitespace), '#pop:2'),
            (r'', Generic.Error, '#pop'),
        ],
        'root': [
            (r'(\n\s*|\t)', Whitespace),
            comment_tuple,
            (r'(package)(\s+)', bygroups(Keyword.Namespace, Text), 'packageOrSystem'),
            (r'(public|private)', Keyword.Namespace),
            with_tuple,
            (r'(annex)(\s+)',  bygroups(Keyword.Namespace, Text), 'annex'),
            (keyword_rex + r'(\s+)', bygroups(Keyword.Type, Text), 'package-declaration'),
            (r'(subcomponents|connections|features|flows)(\s+)', bygroups(Keyword.Namespace, Whitespace)),
            (definition_rex, bygroups(Name.Variable, Punctuation), 'declaration'),
            (r'(properties)(\s*)', bygroups(Keyword.Namespace, Whitespace), 'property-section'),
            (r'(end)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'package-declaration'),
            (r'(property set)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'property-set'),
        ]
    }
