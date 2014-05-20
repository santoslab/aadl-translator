import sys
import re

from pygments.lexer import RegexLexer, include, bygroups
from pygments.token import Error, Punctuation, Literal, Token, \
     Text, Comment, Operator, Keyword, Name, String, Number, Generic, \
     Whitespace

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
    keyword_rex = r'(port|connection|process|thread|data)'
    
    with_tuple = (r'(with)(\s+)', bygroups(Keyword.Namespace, Text), 'with-list')
    text_tuple = (r'[^\S\n]+', Text)
    terminator_tuple = (r'(;)(\s+)', bygroups(Punctuation, Whitespace), '#pop')
    
    tokens = {
         'package': [
             text_tuple,
             (iden_rex, Name.Class, '#pop'),
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
            (r'(in|out|event|data)', Keyword.Constant),
            (r'(flow|path|port|thread)', Keyword.Type),
            (class_iden_rex, bygroups(Name.Class, Punctuation, Name.Entity)),
            (r'(' + iden_rex + r')(\s+)(->|<-|<->)(\s+)('+ iden_rex + r')', bygroups(Name.Variable, Whitespace, Operator, Whitespace, Name.Variable)),
            (iden_rex, Name.Function),
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
            (r'[0-9]+', Number.Integer),
            (r'[0-9]+\.[0-9]*', Number.Float),
            (r'(\s*)(ms)(\s*)', bygroups(Whitespace, Literal, Whitespace)),
            (r'(\s*)(\.\.)(\s+)', bygroups(Whitespace, Operator, Whitespace)),
            (class_iden_rex, bygroups(Name.Class, Punctuation, Name.Constant)),
            (iden_rex, Name.Entity),
        ],
        'applies-to-property-value' : [
            (r'(\s*)(applies to)(\s+)', bygroups(Whitespace, Keyword.Constant, Whitespace), 'applies-to'),
            include('property-value'),
        ],
        'property-section-property-value' : [
        	include('property-value'),
        	terminator_tuple,
        ],
        'property-constant-value' : [
            include('property-value'),
            (r'(;)(\s+)', bygroups(Punctuation, Whitespace), '#pop:2')
        ],
        'property-declaration' : [
            text_tuple,
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace), 'applies-to-property-value'),
            terminator_tuple,
        ],
        'property-constant-declaration' : [
            text_tuple,
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace), 'property-constant-value')
        ],
        'property-set' : [
            with_tuple,
            (r'(' + iden_rex + r')(\s+)(is)(\s+)', bygroups(Name.Class, Whitespace, Keyword.Namespace, Whitespace)),
            (definition_rex + r'(constant)', bygroups(Name.Variable.Global, Punctuation, Keyword.Constant), 'property-constant-declaration'),
            (definition_rex, bygroups(Name.Variable.Global, Punctuation), 'property-declaration'),
            (r'(end)(\s+)(' + iden_rex + r')(;)', bygroups(Keyword.Namespace, Whitespace, Name.Class, Punctuation)),
        ],
        'property-section' : [
            text_tuple,
            (class_iden_rex + r'(\s*)(=>)(\s*)', bygroups(Name.Class, Punctuation, Name.Class, Whitespace, Operator, Whitespace), 'property-section-property-value'),
            (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Class, Whitespace, Operator, Whitespace), 'property-section-property-value'),
            (r'', Generic.Error, '#pop'),
        ],
        'root': [
            (r'(\n\s*|\t)', Whitespace),
            (r'--.*?$', Comment.Single),
            (r'(package)(\s+)', bygroups(Keyword.Namespace, Text), 'package'),
            (r'(public|private)', Keyword.Namespace),
            with_tuple,
            (keyword_rex + r'(\s+)', bygroups(Keyword.Type, Text), 'package-declaration'),
            (r'(subcomponents|connections|features|flows)(\s+)', bygroups(Keyword.Namespace, Whitespace)),
            (definition_rex, bygroups(Name.Variable, Punctuation), 'declaration'),
            (r'(properties)(\s*)', bygroups(Keyword.Namespace, Whitespace), 'property-section'),
            (r'(end)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'package-declaration'),
            (r'(property set)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'property-set'),
        ]
    }